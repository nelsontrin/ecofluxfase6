package br.com.ecoflux.bdd;

import br.com.ecoflux.model.Usuario;
import br.com.ecoflux.repository.AlertaRepository;
import br.com.ecoflux.repository.ColetaRepository;
import br.com.ecoflux.repository.DescarteRepository;
import br.com.ecoflux.repository.PontoColetaRepository;
import br.com.ecoflux.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EcoFluxApiSteps {

    private static final String TEST_LOGIN = "bdd-operador";
    private static final String TEST_PASSWORD = "EcoFlux-BDD-2026!";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AlertaRepository alertaRepository;
    @Autowired private ColetaRepository coletaRepository;
    @Autowired private DescarteRepository descarteRepository;
    @Autowired private PontoColetaRepository pontoColetaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private String token;
    private String pontoId;
    private ResponseEntity<String> response;

    @Before
    public void prepararCenario() {
        token = null;
        pontoId = null;
        response = null;
        alertaRepository.deleteAll();
        coletaRepository.deleteAll();
        descarteRepository.deleteAll();
        pontoColetaRepository.deleteAll();
        if (usuarioRepository.findByLogin(TEST_LOGIN) == null) {
            Usuario usuario = new Usuario();
            usuario.setLogin(TEST_LOGIN);
            usuario.setSenha(passwordEncoder.encode(TEST_PASSWORD));
            usuario.setRole("ADMIN");
            usuarioRepository.save(usuario);
        }
    }

    @Given("que estou autenticado")
    public void estouAutenticado() {
        autenticar();
    }

    @Given("que não estou autenticado")
    public void naoEstouAutenticado() {
        token = null;
    }

    @Given("que existe um ponto ativo com capacidade {double}")
    public void existePontoAtivo(double capacidade) {
        criarPonto(capacidade, 0.0);
    }

    @Given("que existe um ponto com capacidade {double} e volume atual {double}")
    public void existePontoComVolume(double capacidade, double volume) {
        criarPonto(capacidade, volume);
    }

    @Given("que existe um alerta pendente")
    public void existeAlertaPendente() {
        criarPonto(100.0, 95.0);
    }

    @When("envio {word} para {string}")
    public void envioSemPayload(String metodo, String caminho) {
        enviar(metodo, caminho, null);
    }

    @When("envio {word} para {string} com JSON:")
    public void envioComPayload(String metodo, String caminho, String payload) {
        enviar(metodo, caminho, expandir(payload));
    }

    @When("envio {word} para {string} sem token")
    public void envioSemToken(String metodo, String caminho) {
        token = null;
        enviar(metodo, caminho, null);
    }

    @Then("o status HTTP deve ser {int}")
    public void statusDeveSer(int esperado) {
        assertNotNull(response, "Nenhuma requisição HTTP foi executada no cenário");
        assertEquals(esperado, response.getStatusCode().value());
    }

    @Then("o corpo da resposta deve obedecer ao contrato {string}")
    public void respostaObedeceContrato(String arquivo) throws Exception {
        assertNotNull(response, "Nenhuma resposta HTTP disponível");
        assertNotNull(response.getBody(), "A resposta não possui corpo JSON");
        JsonNode schemaNode;
        try (InputStream input = getClass().getResourceAsStream("/contracts/" + arquivo)) {
            assertNotNull(input, "Contrato não encontrado: " + arquivo);
            schemaNode = objectMapper.readTree(input);
        }
        JsonNode instance = objectMapper.readTree(response.getBody());
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode);
        Set<ValidationMessage> erros = schema.validate(instance);
        assertTrue(erros.isEmpty(), "Falhas de contrato em " + arquivo + ": " + erros);
    }

    @Then("o campo {string} deve ser {string}")
    public void campoDeveSer(String campo, String esperado) throws Exception {
        JsonNode valor = corpoJson().path(campo);
        assertFalse(valor.isMissingNode(), "Campo ausente: " + campo);
        assertEquals(esperado, valor.asText());
    }

    @Then("o campo numérico {string} deve ser {double}")
    public void campoNumericoDeveSer(String campo, double esperado) throws Exception {
        JsonNode valor = corpoJson().path(campo);
        assertTrue(valor.isNumber(), "Campo não numérico: " + campo);
        assertEquals(esperado, valor.asDouble(), 0.0001);
    }

    @Then("o campo {string} deve conter texto")
    public void campoDeveConterTexto(String campo) throws Exception {
        String valor = corpoJson().path(campo).asText();
        assertFalse(valor.isBlank(), "Campo vazio: " + campo);
    }

    @Then("a resposta deve ser uma lista não vazia")
    public void respostaEhListaNaoVazia() throws Exception {
        JsonNode lista = corpoJson();
        assertTrue(lista.isArray(), "A resposta não é um array JSON");
        assertFalse(lista.isEmpty(), "Esperava ao menos um item na lista");
    }

    @Then("a lista de alertas pendentes deve estar vazia")
    public void listaAlertasPendentesVazia() throws Exception {
        enviar("GET", "/alertas/pendentes", null);
        assertEquals(200, response.getStatusCode().value());
        JsonNode lista = corpoJson();
        assertTrue(lista.isArray());
        assertTrue(lista.isEmpty(), "Ainda há alertas pendentes: " + lista);
    }

    @Then("o primeiro alerta deve ter nível {string}")
    public void primeiroAlertaTemNivel(String nivel) throws Exception {
        JsonNode lista = corpoJson();
        assertTrue(lista.isArray() && !lista.isEmpty(), "A lista não contém alertas");
        assertEquals(nivel, lista.get(0).path("nivel").asText());
    }

    @Then("o corpo da resposta deve estar vazio")
    public void corpoVazio() {
        assertTrue(response == null || response.getBody() == null || response.getBody().isBlank(),
                "Esperava resposta sem corpo, mas recebi: " + (response == null ? null : response.getBody()));
    }

    @Then("o volume atual do ponto deve ser {double}")
    public void volumeAtualDoPonto(double esperado) throws Exception {
        enviar("GET", "/pontos-coleta/{id}", null);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(esperado, corpoJson().path("volumeAtual").asDouble(), 0.0001);
    }

    @Then("o ponto deve continuar consultável e inativo")
    public void pontoContinuaInativo() throws Exception {
        enviar("GET", "/pontos-coleta/{id}", null);
        assertEquals(200, response.getStatusCode().value());
        assertFalse(corpoJson().path("ativo").asBoolean());
    }

    private void autenticar() {
        String body = "{\"login\":\"" + TEST_LOGIN + "\",\"senha\":\"" + TEST_PASSWORD + "\"}";
        enviar("POST", "/auth/login", body);
        assertEquals(200, response.getStatusCode().value(), "Falha ao obter JWT para o cenário");
        try {
            token = corpoJson().path("token").asText();
            assertFalse(token.isBlank(), "O endpoint de login devolveu token vazio");
        } catch (Exception e) {
            throw new AssertionError("Não foi possível ler o token de autenticação", e);
        }
    }

    private void criarPonto(double capacidade, double volumeInicial) {
        garantirAutenticacao();
        String nome = "BDD-" + UUID.randomUUID();
        String body = "{\"nome\":\"" + nome + "\",\"endereco\":\"Rua de Teste, 123\",\"capacidadeMaxima\":" + capacidade + "}";
        enviar("POST", "/pontos-coleta", body);
        assertEquals(201, response.getStatusCode().value(), "Não foi possível preparar o ponto de coleta");
        try {
            pontoId = corpoJson().path("id").asText();
            assertFalse(pontoId.isBlank(), "O cadastro do ponto não retornou ID");
        } catch (Exception e) {
            throw new AssertionError("Não foi possível ler o ID do ponto criado", e);
        }
        if (volumeInicial > 0) {
            registrarDescarte(volumeInicial);
            assertEquals(201, response.getStatusCode().value(), "Não foi possível preparar o volume inicial");
        }
    }

    private void registrarDescarte(double volume) {
        String body = "{\"pontoColetaId\":" + pontoId + ",\"tipoResiduo\":\"PLASTICO\",\"volume\":" + volume + ",\"reciclavel\":true}";
        enviar("POST", "/descartes", body);
    }

    private void garantirAutenticacao() {
        if (token == null || token.isBlank()) autenticar();
    }

    private void enviar(String metodo, String caminho, String payload) {
        String rota = expandir(caminho);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null && !token.isBlank()) headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        response = restTemplate.exchange(rota, HttpMethod.valueOf(metodo.toUpperCase()), entity, String.class);
    }

    private String expandir(String valor) {
        String id = pontoId == null ? "999999999" : pontoId;
        return valor.replace("{{pontoId}}", id).replace("{id}", id);
    }

    private JsonNode corpoJson() throws Exception {
        assertNotNull(response, "Nenhuma resposta HTTP disponível");
        assertNotNull(response.getBody(), "A resposta não possui corpo");
        return objectMapper.readTree(response.getBody());
    }
}
