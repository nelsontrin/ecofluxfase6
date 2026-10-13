# EcoFlux — Gestão Automatizada de Resíduos

O EcoFlux é uma API REST para apoiar o controle do ciclo de resíduos: pontos de coleta, descartes, coletas e alertas de capacidade. O projeto foi desenvolvido com foco em sustentabilidade, rastreabilidade e integridade dos dados.

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.4.5
- Spring Web, Spring Data JPA, Spring Security e Bean Validation
- H2 persistente em arquivo
- Flyway para migrações
- JWT para autenticação
- Maven
- Docker e Docker Compose
- GitHub Actions

## Como executar localmente com Docker

### Pré-requisitos

- Docker Engine com Docker Compose
- Portas 8080 disponíveis
- Pelo menos 2 GB de memória disponíveis para o Docker

### Configuração

1. Copie o arquivo de exemplo:

   ```bash
   cp .env.example .env
   ```

2. Edite `.env` e troque `DB_PASSWORD` e `JWT_SECRET`. O arquivo `.env` não deve ser versionado.

3. Suba a aplicação:

   ```bash
   docker compose up --build -d
   ```

4. Consulte os logs:

   ```bash
   docker compose logs -f ecoflux-app
   ```

5. Consulte a API:

   ```bash
   curl http://localhost:8080/pontos-coleta
   ```

6. Pare os serviços quando terminar:

   ```bash
   docker compose down
   ```

O banco H2 é armazenado em um volume Docker chamado `ecoflux-data`. Para remover também os dados locais, use `docker compose down -v`.

## Execução sem Docker

A aplicação usa H2 persistente no diretório `/data` por padrão. Para executar localmente, defina um caminho gravável:

```bash
export DB_URL='jdbc:h2:file:./data/ecoflux-db;DB_CLOSE_ON_EXIT=FALSE'
export DB_USER=sa
export DB_PASSWORD=senha-local
export JWT_SECRET=chave-local
./mvnw spring-boot:run
```

## Testes

Os testes usam um perfil `test` com H2 em memória e não dependem de um banco externo:

```bash
./mvnw test
```

O conjunto inclui teste de carregamento do contexto e testes unitários das regras de descarte, alerta, coleta e atualização de inventário.

## Pipeline CI/CD

O workflow está em `.github/workflows/ci-cd.yml` e possui quatro partes:

1. **Build e testes:** configura o Java 17, executa `./mvnw test`, gera o JAR e publica o artefato.
2. **Imagem:** em pushes para `main` e tags `v*.*.*`, constrói a imagem Docker e publica no GitHub Container Registry.
3. **Staging:** após um push na `main`, conecta por SSH à VM, atualiza o Compose, baixa a imagem e inicia a aplicação na porta configurada.
4. **Produção:** após uma tag de versão, repete o processo usando o ambiente protegido `production` e outra pasta/porta na mesma VM.

Os deploys permanecem desativados até que `ENABLE_STAGING=true` e `ENABLE_PRODUCTION=true` sejam cadastrados como variáveis dos ambientes do GitHub.

### Secrets necessários no GitHub

Os ambientes `staging` e `production` devem possuir os secrets abaixo:

| Secret | Uso |
|---|---|
| `HOST` | IP público da VM Azure |
| `USER` | Usuário SSH, por exemplo `azureuser` |
| `SSH_KEY` | Conteúdo da chave privada SSH |
| `APP_DIR` | `/opt/ecoflux-staging` ou `/opt/ecoflux-production` |
| `APP_PORT` | `8080` em staging e `8081` em produção |
| `COMPOSE_PROJECT_NAME` | `ecoflux-staging` ou `ecoflux-production` |
| `DB_USER` | Usuário H2, normalmente `sa` |
| `DB_PASSWORD` | Senha do banco H2 |
| `JWT_SECRET` | Chave de assinatura dos tokens |
| `GHCR_READ_TOKEN` | Token de leitura do GitHub Container Registry |

Os valores de banco e JWT devem ser diferentes entre os ambientes. Não coloque credenciais reais no repositório.

### Variáveis dos ambientes

- No ambiente `staging`, crie `ENABLE_STAGING=true`.
- No ambiente `production`, crie `ENABLE_PRODUCTION=true`.

## Containerização

O `Dockerfile` usa duas etapas:

- `maven:3.9.9-eclipse-temurin-17` compila o projeto e gera o JAR;
- `eclipse-temurin:17-jre-alpine` executa somente o artefato final com um usuário sem privilégios de administrador.

O `docker-compose.yml` orquestra a API, o volume persistente do H2 e a rede privada `ecoflux-network`. O projeto Compose é diferente em staging e produção para manter containers e volumes isolados na mesma VM.

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

Os prints e links dos pipelines e ambientes devem ser adicionados à documentação depois da execução real:

- resultado do job `Build e testes`;
- imagem publicada no GHCR;
- logs do deploy em staging;
- logs do deploy em produção;
- resposta de `/pontos-coleta` nas duas portas;
- saída de `docker compose ps` nos dois diretórios.

Não são incluídos prints fictícios.

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
| Docker Compose com serviço da aplicação e banco persistente | Preparado |
| Pipeline com etapas de build e testes | Preparado |
| Pipeline com deploy parametrizado para staging | Requer VM e secrets |
| Pipeline com deploy parametrizado para produção | Requer VM e secrets |
| README com instruções | Preparado |
| Documentação técnica | Preparada com campos de evidência |
| Deploy executado em staging e produção | Pendente de execução real |
