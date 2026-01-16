# Trelix - Real-Time Team Collaboration Platform

A **production-ready** backend for team collaboration with real-time messaging, task management, and event-driven notifications. Built with enterprise-grade reliability patterns.

---

## 🏗️ Architecture Overview

```
                    ┌─────────────────────────────────────────────────────────────────┐
                    │                        CLIENTS                                  │
                    │              (Web App / Mobile / REST API)                      │
                    └──────────────────────────┬──────────────────────────────────────┘
                                               │
        ┌──────────────────────────────────────┼──────────────────────────────────────┐
        │                                      │                                      │
   REST APIs                              WebSocket                              Swagger UI
        │                                      │                                      │
        ▼                                      ▼                                      │
┌───────────────┐         ┌──────────────────────────────────┐                        │
│  Controllers  │◄────────│        RateLimitFilter           │◄───────────────────────┘
│               │         │    (100 req/min per user)        │
└───────┬───────┘         └──────────────────────────────────┘
        │                              │
        │         ┌────────────────────┼────────────────────┐
        │         │                    │                    │
        │    Redis Cache         JWT Auth Filter       Circuit Breaker
        │    (Caching)           (Security)            (Resilience4j)
        │         │                    │                    │
        ▼         ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SERVICE LAYER                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            │
│  │ AuthService  │  │ TeamService  │  │ TaskService  │  │ ChatService  │            │
│  │              │  │  @Cacheable  │  │              │  │              │            │
│  │ Refresh Token│  │  @CacheEvict │  │              │  │              │            │
│  │   Rotation   │  │              │  │              │  │              │            │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘            │
└────────────────────────────────────────┬────────────────────────────────────────────┘
                                         │
        ┌────────────────────────────────┼────────────────────────────────┐
        │                                │                                │
        ▼                                ▼                                ▼
┌───────────────┐              ┌───────────────┐              ┌───────────────────────┐
│  PostgreSQL   │              │    Redis      │              │      Kafka            │
│               │              │               │              │                       │
│ - Users       │              │ - Caching     │              │ - Notifications       │
│ - Teams       │              │ - Rate Limits │              │ - @RetryableTopic     │
│ - Projects    │              │               │              │ - Dead Letter Queue   │
│ - Tasks       │              │               │              │                       │
│ - Messages    │              │               │              │                       │
│ - Refresh     │              │               │              │                       │
│   Tokens      │              │               │              │                       │
└───────────────┘              └───────────────┘              └───────────────────────┘
```

---

## ⚡ Tech Stack

| Layer | Technology |
|-------|------------|
| **Framework** | Spring Boot 3.3, Java 21 |
| **Security** | JWT (Access + Refresh tokens with rotation) |
| **Database** | PostgreSQL with indexed FKs |
| **Caching** | Redis with JSON serialization |
| **Messaging** | Apache Kafka (KRaft mode) with retry + DLQ |
| **Real-Time** | WebSocket + STOMP |
| **Resilience** | Resilience4j Circuit Breaker |
| **API Docs** | Swagger/OpenAPI |

---

## 🛡️ Production-Grade Features

### 1. Redis Caching with Graceful Degradation
- **JSON serialization** for readable cache entries
- **Cache-aside pattern** with `@Cacheable` and `@CacheEvict`
- **Graceful fallback** when Redis is unavailable (CacheErrorHandler)

```java
@Cacheable(value = "projects", key = "#projectId")
public ProjectDetailResponse getProjectById(UUID projectId) { ... }

@CacheEvict(value = "projects", key = "#projectId")
public void updateProject(UUID projectId, ...) { ... }
```

### 2. Stateful Refresh Tokens with Theft Detection
- **Token rotation**: Each refresh generates new token, old is revoked
- **Family-based revocation**: Detects token reuse → revokes entire session
- **SHA-256 hashed** storage in database
- **Scheduled cleanup** of expired tokens

```
Login → Access Token (15 min) + Refresh Token (7 days)
         ↓
Use refresh → New tokens issued, old revoked
         ↓
Attacker uses old token → DETECTED → Entire family revoked
```

### 3. API Rate Limiting
- **Fixed Window Counter** algorithm using Redis
- **100 requests/minute** per authenticated user
- Returns `429 Too Many Requests` with JSON error

```json
{
  "errorCode": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit exceeded. Please try again later."
}
```

### 4. Circuit Breaker (Resilience4j)
- Protects external service calls (Cloudinary)
- **States**: CLOSED → OPEN → HALF_OPEN
- Configurable thresholds and timeouts
- Actuator endpoint: `/actuator/circuitbreakers`

```java
@CircuitBreaker(name = "cloudinary", fallbackMethod = "uploadFallback")
public String uploadFile(MultipartFile file) { ... }
```

### 5. Kafka Retry + Dead Letter Queue
- **Exponential backoff**: 1s → 2s → 4s
- **4 attempts** before sending to DLT
- **@DltHandler** for failed message processing

```java
@RetryableTopic(attempts = "4", backoff = @Backoff(delay = 1000, multiplier = 2))
@KafkaListener(topics = "trelix-notifications")
public void consume(NotificationEvent event) { ... }

@DltHandler
public void handleDlt(NotificationEvent event) { ... }
```

### 6. Database Optimization
- **Indexed foreign keys** for fast lookups:
  - `tasks.project_id`
  - `messages.channel_id`
  - `project_members.project_id`
  - `team_users.team_id`

---

## 📋 Core Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access (OWNER, ADMIN, MEMBER)
- Stateful refresh token rotation

### Team Collaboration
- Create teams with member management
- Projects scoped to teams
- Tasks with status tracking, priority, assignments

### Real-Time Communication
- WebSocket chat via STOMP
- Team channels (PUBLIC/PRIVATE)
- Direct messages (1:1)
- Live notifications

### Event-Driven Notifications
- Kafka async processing
- Events: `TASK_ASSIGNED`, `TEAM_INVITE`, `PROJECT_INVITE`, `TASK_STATUS_CHANGED`

---

## 🚀 Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- PostgreSQL
- Redis
- Kafka

### Run Locally

```bash
# 1. Start dependencies
docker-compose up -d

# 2. Run the application
./mvnw spring-boot:run

# 3. Access Swagger UI
open http://localhost:8080/api/swagger-ui.html

# 4. Test WebSocket
open http://localhost:8080/api/websocket-test.html
```

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/auth/register` | Create new account |
| POST | `/v1/auth/login` | Get access + refresh tokens |
| POST | `/v1/auth/refresh` | Rotate refresh token |

### Teams & Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/teams` | Create team |
| GET | `/v1/teams` | List user's teams |
| POST | `/v1/teams/{id}/members` | Add team member |
| GET | `/v1/projects` | List projects by team |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/tasks` | Create task |
| PATCH | `/v1/tasks/{id}/status` | Update status (triggers Kafka) |
| POST | `/v1/tasks/{id}/members` | Assign user (triggers Kafka) |

### WebSocket
```
Connect:   ws://localhost:8080/api/ws
Subscribe: /topic/channel.{channelId}
Send:      /app/chat.{channelId}
```

---

## ⚙️ Configuration

```properties
# JWT
jwt.secret=your-secret-key
jwt.access-token-expiration=900000       # 15 minutes
jwt.refresh-token-expiration-days=7

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Circuit Breaker
resilience4j.circuitbreaker.instances.cloudinary.sliding-window-size=10
resilience4j.circuitbreaker.instances.cloudinary.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.cloudinary.wait-duration-in-open-state=10s
```

---

## 🗂️ Project Structure

```
src/main/java/com/trelix/trelix_app/
├── config/          # Security, Redis, WebSocket, Kafka
├── controller/      # REST + WebSocket controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities with indexes
├── enums/           # Status, Role, ErrorCode enums
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── filter/          # JWT Auth, Rate Limiting
├── repository/      # Data access layer
├── scheduler/       # Token cleanup jobs
├── service/         # Business logic with caching
└── util/            # JWT utilities
```

---

## 📊 Database Schema

```
Users ──┬── Teams (via TeamUser)
        ├── Projects (via ProjectMember)
        ├── Tasks (via TaskMember)
        ├── Channels (via ChannelMember)
        ├── DirectMessages
        ├── Notifications
        └── RefreshTokens (with familyId)

Teams ── Projects ── Tasks
      └── Channels ── Messages
```

[View Full DB Diagram](https://app.eraser.io/workspace/cKEDj34DX1uLgQPNa98N?origin=share)

---

## 🔧 Monitoring Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health |
| `/actuator/circuitbreakers` | Circuit breaker states |

---

## 👨‍💻 Author

Built as a demonstration of **production-grade backend architecture** using modern Spring Boot patterns.
