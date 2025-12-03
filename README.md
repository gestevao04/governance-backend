# 🎯 Governance & Cost Control – Spring Boot + Kotlin

Fully featured backend for governance and cost tracking of AI requests, built with Spring Boot 3.3, Kotlin 2.0, and clean architecture.

## 📋 Features

- ✅ **API Key authentication** (header `x-api-key`)
- ✅ **Structured JSON logging** (Logback + Logstash Encoder)
- ✅ **Automatic cost calculation** per model and token
- ✅ **Cost alerts** via webhook when threshold is exceeded
- ✅ **SQLite** storage using Spring Data JDBC
- ✅ **Daily usage reports** with model-level aggregations
- ✅ **Clean architecture** (Controllers → Services → Repositories)

## 🛠️ Stack Tecnológica

- **Kotlin** 2.0
- **Spring Boot** 3.3.0
- **Spring Data JDBC** (no JPA)
- **SQLite** 3.45.3
- **Logback + Logstash Encoder** 7.4
- **Gradle Kotlin DSL**
- **JDK** 21

## 📁 Project Structure

```
src/main/kotlin/com/governanca/
├── config/
│   ├── ApiKeyFilter.kt          # Authentication filter
│   └── LoggingConfig.kt         # Logging configuration
├── controllers/
│   ├── ProcessController.kt     # POST /process
│   └── UsageController.kt       # GET /usage/daily
├── services/
│   ├── ProcessService.kt        # Processing logic
│   ├── UsageService.kt          # Aggregations
│   ├── AlertService.kt          # Webhook alerts
│   └── CostService.kt           # Cost calculations
├── repository/
│   └── CacheService.kt          # Spring Data JDBC repo
├   └── entities/
│       └── RequestEntity.kt     # Request entity
├── utils/
│   ├── HashUtil.kt              # SHA-256 for API keys
│   └── DateUtils.kt             # Date utilities
└── dto/
    ├── ProcessRequest.kt        # Request payload
    ├── ProcessResponse.kt       # Response payload
    ├── DailyUsageResponse.kt    # Daily report payload
    └── AlertWebhook.kt          # Webhook Payload 
```

## 🚀 Running the Application

### Requisitos

- JDK 21+
- Gradle 8.7+ (or included wrapper)
- Docker (optional)

### 1️⃣ Set Environment Variables

```bash
export API_KEY="your-super-secret-api-key"
export WEBHOOK_URL="http://localhost:5678/webhook/cost-alert"
```

### 2️⃣ Run Locally

```bash
./gradlew clean build
./gradlew bootRun
```

### 3️⃣ Run with Docker

```bash
docker build -t governanca-custos .

docker run -p 8080:8080 \
  -e API_KEY="your-api-key" \
  -e WEBHOOK_URL="http://host.docker.internal:5678/webhook" \
  -v $(pwd)/data:/app/data \
  governanca-custos
```

### 4️⃣ Run with Docker Compose

```bash
echo "API_KEY=your-api-key" > .env
echo "WEBHOOK_URL=http://localhost:5678/webhook" >> .env

docker-compose up -d
```

## 📡 Endpoints

### POST `/process`

Processes an AI request and computes its cost.

**Headers:**
```
x-api-key: your-api-key
Content-Type: application/json
```

**Request Body:**
```json
{
  "model": "gpt-4.1",
  "tokens": 1200,
  "input": "Optional input text",
  "output": "Optional output text"
}
```

**Response:**
```json
{
  "status": "ok",
  "requestCost": 0.0024
}
```

**Example cURL:**
```bash
curl -X POST http://localhost:8080/process \
  -H "x-api-key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4.1",
    "tokens": 1200,
    "input": "Analyze this text",
    "output": "Analysis complete"
  }'
```

### GET `/usage/daily`

Returns daily usage aggregations.

**Headers:**
```
x-api-key: your-api-key
```

**Query Parameters:**
- `date` (optional): Date in format `YYYY-MM-DD`

**Response:**
```json
{
  "date": "2025-12-02",
  "totalRequests": 14,
  "totalCost": 0.210000,
  "costByModel": {
    "gpt-4.1": 0.150000,
    "gpt-4o-mini": 0.060000
  },
  "summary": "On 2025-12-02, the system processed 14 requests costing $0.210000."
}
```

**Example cURL:**
```bash
curl -X GET http://localhost:8080/usage/daily \
  -H "x-api-key: your-api-key"

curl -X GET "http://localhost:8080/usage/daily?date=2025-12-01" \
  -H "x-api-key: your-api-key"
```

## 💰 Models e Prices

| Model       | Token Cost |
|-------------|------------|
| gpt-4.1     | $0.000002  |
| gpt-4o-mini | $0.0000006 |
| sonnet      | $0.000003  |

## 🔔 Cost Alerts

**Webhook Payload:**
```json
{
  "type": "cost_alert",
  "amount": 0.015,
  "model": "gpt-4.1",
  "tokens": 300
}
```

## 📊 Structured Logging

All logs are emitted in structured JSON:

```json
{
  "timestamp": "2025-12-02T14:30:45.123Z",
  "level": "INFO",
  "logger": "com.governanca.services.ProcessService",
  "message": {
    "action": "process_request",
    "route": "/process",
    "apiKeyHash": "a3f8b2...",
    "model": "gpt-4.1",
    "tokens": 1200,
    "cost": 0.0024,
    "executionTimeMs": 15
  }
}
```

## 🗄️ Database

SQLite is used with a file at governanca.db.

**Schema:**
```sql
CREATE TABLE requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp TEXT NOT NULL,
    model TEXT NOT NULL,
    tokens INTEGER NOT NULL,
    cost REAL NOT NULL,
    input_text TEXT,
    output_text TEXT,
    api_key_hash TEXT NOT NULL
);
```

## 🔧 Configurations

Edit `src/main/resources/application.yml`:

```yaml
governanca:
  api-key: ${API_KEY}           # API key para autenticação
  webhook-url: ${WEBHOOK_URL}    # URL do webhook de alertas
  cost-threshold: 0.01           # Threshold para alertas
```

## 🧪 Testing an Application

### 1. Processing a request

```bash
curl -X POST http://localhost:8080/process \
  -H "x-api-key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4.1",
    "tokens": 5000,
    "input": "Test input"
  }'
```

### 2. Verifying Daily Usage

```bash
curl -X GET http://localhost:8080/usage/daily \
  -H "x-api-key: your-api-key"
```

### 3. Testing invalid authentication

```bash
curl -X POST http://localhost:8080/process \
  -H "x-api-key: wrong-key" \
  -H "Content-Type: application/json" \
  -d '{"model": "gpt-4.1", "tokens": 1000}'
# Retorna: 401 Unauthorized
```

## 📦 Build for Production

```bash
./gradlew clean build -x test

java -jar build/libs/governanca-custos-1.0.0.jar
```

## 🐛 Troubleshooting

### Error: "API Key is required"
- Ensure you’re sending header `x-api-key`

### Error: "Invalid API Key"
- Check `API_KEY` environment value.

### Webhook not firing
- Confirm `WEBHOOK_URL` is set.
- Check logs.

## 🤝 Contribuindo

PRs are welcome! For major changes, please open an issue first.

---

**Built with ❤️ using Kotlin + Spring Boot**