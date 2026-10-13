# EcoFlux — Gestão Automatizada de Resíduos

O EcoFlux é uma API REST para apoiar o controle do ciclo de resíduos: pontos de coleta, descartes, coletas e alertas de capacidade. O projeto foi desenvolvido com foco em sustentabilidade, rastreabilidade e integridade dos dados.

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, Spring Security e Bean Validation
- Oracle Database XE 21c
- Flyway para migrações
- JWT para autenticação
- Maven
- Docker e Docker Compose
- GitHub Actions

## Como executar localmente com Docker

### Pré-requisitos

- Docker Engine com Docker Compose
- Portas 8080 e 1521 disponíveis
- Aproximadamente 2 minutos para a inicialização inicial do Oracle XE

### Configuração

1. Copie o arquivo de exemplo:

   ```bash
   cp .env.example .env
   ```

2. Edite `.env` e defina as senhas e a chave JWT. O arquivo `.env` não deve ser versionado.

3. Suba os serviços:

   ```bash
   docker compose up --build -d
   ```

4. Consulte os logs:

   ```bash
   docker compose logs -f ecoflux-app
   ```

5. Pare os serviços quando terminar:

   ```bash
   docker compose down
   ```

Para remover também o volume persistente do Oracle, use `docker compose down -v`. Essa ação apaga os dados locais do banco.

## Execução sem Docker

A aplicação espera um Oracle disponível e as variáveis abaixo:

```bash
export ORACLE_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
export ORACLE_USER=ecoflux
export ORACLE_PASSWORD=senha-local
export JWT_SECRET=chave-local
./mvnw spring-boot:run
```

## Testes

Os testes do pipeline usam um perfil `test` com H2 em memória. Assim, o build e os testes não dependem de um Oracle externo.

```bash
./mvnw test
```

O conjunto inclui teste de carregamento do contexto e testes unitários das regras de descarte, alerta, coleta e atualização de inventário.

## Pipeline CI/CD

O workflow está em `.github/workflows/ci-cd.yml` e possui quatro partes:

1. **Build e testes:** configura o Java 17, executa `./mvnw test`, gera o JAR e publica o artefato.
2. **Imagem:** em pushes para `main` e tags `v*.*.*`, constrói a imagem Docker e publica no GitHub Container Registry.
3. **Staging:** após um push na `main`, conecta por SSH ao servidor de staging, atualiza o Compose, baixa a imagem e reinicia os serviços.
4. **Produção:** após uma tag de versão, repete o processo usando o ambiente protegido `production` do GitHub.

### Secrets necessários no GitHub

Os ambientes `staging` e `production` devem possuir os secrets abaixo:

| Secret | Uso |
|---|---|
| `HOST` | Endereço do servidor remoto |
| `USER` | Usuário SSH |
| `SSH_KEY` | Chave privada do usuário de deploy |
| `APP_DIR` | Diretório do Compose no servidor |
| `APP_PORT` | Porta pública da API, por exemplo `8080` em staging e `8081` em produção |
| `ORACLE_PORT` | Porta local do Oracle, por exemplo `1521` em staging e `1522` em produção |
| `COMPOSE_PROJECT_NAME` | Nome isolado do Compose, por exemplo `ecoflux-staging` ou `ecoflux-production` |
| `ORACLE_SYS_PASSWORD` | Senha administrativa do Oracle XE |
| `ORACLE_USER` | Usuário da aplicação no Oracle |
| `ORACLE_PASSWORD` | Senha do usuário da aplicação |
| `JWT_SECRET` | Chave de assinatura dos tokens |
| `GHCR_READ_TOKEN` | Token de leitura do GitHub Container Registry no servidor |

Os valores são diferentes em staging e produção. Não coloque credenciais reais no repositório.

Para evitar falha no primeiro push, os deploys começam desativados. Depois de cadastrar
os secrets e testar os servidores, crie estas variáveis nos ambientes correspondentes:

- `ENABLE_STAGING=true` no ambiente `staging`;
- `ENABLE_PRODUCTION=true` no ambiente `production`.

Enquanto essas variáveis não existirem com o valor `true`, o workflow executará build,
testes e publicação da imagem, mas pulará os jobs de deploy.

## Containerização

O `Dockerfile` usa duas etapas:

- `maven:3.9.9-eclipse-temurin-17` compila o projeto e gera o JAR;
- `eclipse-temurin:17-jre-alpine` executa somente o artefato final com um usuário sem privilégios de administrador.

O `docker-compose.yml` orquestra:

- `oracle-db`, com Oracle XE, healthcheck e volume `oracle-data`;
- `ecoflux-app`, com a API, dependência condicional da saúde do banco, rede privada e variáveis de ambiente.

## API e autenticação

A API usa JWT. O login é feito em `POST /auth/login`:

```json
{
  "login": "admin",
  "senha": "admin123"
}
```

Os endpoints de consulta de pontos de coleta são públicos. As operações de criação, descarte, coleta e alertas exigem o header:

```text
Authorization: Bearer <token>
```

Rotas principais:

| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/login` | Gera token JWT |
| GET | `/pontos-coleta` | Lista pontos |
| POST | `/pontos-coleta` | Cria ponto |
| POST | `/descartes` | Registra descarte |
| GET | `/descartes` | Lista descartes |
| POST | `/coletas` | Registra coleta |
| GET | `/coletas` | Lista coletas |
| GET | `/alertas` | Lista alertas |
| GET | `/alertas/pendentes` | Lista alertas em aberto |

## Regras de negócio demonstradas

- Não é permitido descartar em ponto inativo.
- O volume descartado não pode ultrapassar a capacidade do ponto.
- A partir de 85% da capacidade, o sistema gera alerta médio.
- A partir de 95%, o alerta é classificado como alto.
- Uma coleta reduz o inventário do ponto.
- Uma coleta resolve os alertas pendentes daquele ponto.

## Evidências de execução

Os prints do pipeline e dos ambientes devem ser adicionados à documentação depois da execução no repositório e nos servidores escolhidos. O pipeline foi preparado para produzir:

- resultado do job `Build e testes`;
- imagem publicada no GHCR;
- logs dos jobs `Deploy em staging` e `Deploy em produção`;
- resposta do endpoint `/pontos-coleta` nos dois ambientes;
- saída de `docker compose ps` nos dois servidores.

Não são incluídos prints fictícios. A evidência deve corresponder às execuções reais do GitHub Actions.

## Integrantes

- Alana de Menezes Pereira — RM 562562
- Carlos Eduardo Gonçalves de França — RM 561206
- Gustavo Cruz de Oliveira — RM 565633
- Nelson Trindade Teixeira — RM 563946
- Wendel de Santana Santos — RM 566354

## Checklist da atividade

| Item | Situação |
|---|---|
| Projeto compactado com estrutura organizada | Preparado |
| Dockerfile funcional | Preparado |
| Docker Compose com serviço da aplicação e banco | Preparado |
| Pipeline com etapas de build e testes | Preparado |
| Pipeline com deploy parametrizado para staging | Requer secrets e servidor |
| Pipeline com deploy parametrizado para produção | Requer secrets e servidor |
| README com instruções | Preparado |
| Documentação técnica | Preparada com campos de evidência |
| Deploy executado em staging e produção | Pendente de servidores e credenciais |
