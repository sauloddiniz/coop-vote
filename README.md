# Coop-Vote - Sistema de Votação Cooperativa

Este é um sistema de gerenciamento de pautas e sessões de votação para cooperativas, desenvolvido com Spring Boot, RabbitMQ e Banco de Dados Oracle.

## 🚀 Como Executar a Aplicação

### 📋 Pré-requisitos
* Java 21
* Docker e Docker Compose
* Gradle (opcional, pode usar o `./gradlew` incluso)

### 🐳 1. Subir as Dependências (Docker)
A aplicação depende do Oracle DB, RabbitMQ e de um serviço externo de validação. Execute o comando abaixo na raiz do projeto:

```bash
docker-compose up -d
```

As dependências estarão disponíveis em:
* **Oracle DB**: `localhost:1521`
* **RabbitMQ**: `localhost:5672`
* **Validador de CPF**: `localhost:8081`

### ☕ 2. Executar a Aplicação
Após as dependências estarem prontas, execute:

```bash
./gradlew bootRun
```

A API estará disponível em `http://localhost:8080`.

## 📖 Documentação da API (Swagger)
Com a aplicação rodando, acesse a documentação interativa:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🛠️ Como Utilizar a Aplicação

O fluxo básico de votação segue estes passos:

### 1. Criar uma Pauta
`POST /api/v1/pauta`
```json
{
  "titulo": "Aumento do capital social",
  "descricao": "Votação sobre o aumento do capital em 10%"
}
```

### 2. Abrir uma Sessão de Votação
`POST /api/v1/sessao-votacao/abrir`
```json
{
  "pautaId": 1,
  "dataFechamento": "2026-01-20T20:43:21.883Z"
}
```

### 3. Registrar um Voto
`POST /api/v1/voto`
```json
{
  "pautaId": 1,
  "associadoId": "73383496035",
  "escolha": "SIM"
}
```

> [!IMPORTANT]
> **Observação Importante:** O campo `associadoId` **DEVE** ser um CPF válido. A aplicação consulta um serviço externo de validação; se o documento for inválido, o voto não será permitido.

### 4. Consultar Resultado
`GET /api/v1/pauta/{id}/resultado`

## 📊 Observabilidade e Métricas
A aplicação possui suporte ao Actuator e Micrometer:
* **Health Check**: `http://localhost:8080/actuator/health`
* **Métricas (Timed)**: `http://localhost:8080/actuator/metrics`
