# Trelix - Real-Time Team Collaboration Platform

A production-ready backend for team collaboration with real-time messaging, task management, and event-driven notifications.

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Framework** | Spring Boot 3.3, Java 21 |
| **Security** | JWT (Access + Refresh tokens) |
| **Database** | PostgreSQL |
| **Messaging** | Apache Kafka (KRaft mode) |
| **Real-Time** | WebSocket + STOMP |
| **API Docs** | Swagger/OpenAPI |

## Architecture

```
                    ┌─────────────────────────────────────────────────────┐
                    │                   CLIENTS                           │
                    │         (REST API / WebSocket / Mobile)             │
                    └──────────────────────┬──────────────────────────────┘
                                           │
              ┌────────────────────────────┼────────────────────────────┐
              │                            │                            │
         REST APIs                    WebSocket                    Kafka
              │                            │                            │
              ▼                            ▼                            ▼
┌─────────────────────┐      ┌─────────────────────┐      ┌─────────────────────┐
│   Controllers       │      │   STOMP Broker      │      │   KafkaProducer     │
│  (Auth, Team, Task) │      │   /ws endpoint      │      │   (Async Events)    │
└──────────┬──────────┘      └──────────┬──────────┘      └──────────┬──────────┘
           │                            │                            │
           └────────────────────────────┼────────────────────────────┘
                                        │
                               ┌────────┴────────┐
                               │   Services      │
                               │  (Business      │
                               │   Logic)        │
                               └────────┬────────┘
                                        │
                               ┌────────┴────────┐
                               │  Repositories   │
                               │  (JPA/Hibernate)│
                               └────────┬────────┘
                                        │
                               ┌────────┴────────┐
                               │   PostgreSQL    │
                               └─────────────────┘
```

## Features

### Core APIs
- **Authentication** - Register, Login, JWT Refresh, Logout
- **Teams** - Create teams, manage members with roles (OWNER, ADMIN, MEMBER)
- **Projects** - Team-scoped projects with member management
- **Tasks** - Full CRUD with status tracking, priority, assignments
- **Channels** - Team chat channels (PUBLIC/PRIVATE)
- **Direct Messages** - 1:1 private conversations
- **Notifications** - In-app notification system

### Real-Time Features
- **WebSocket Chat** - Instant message delivery via STOMP
- **Live Notifications** - Push notifications to users in real-time

### Event-Driven Architecture
- **Kafka Integration** - Async notification processing
- **Event Types**: `TASK_ASSIGNED`, `TEAM_INVITE`, `PROJECT_INVITE`, `TASK_STATUS_CHANGED`

## API Endpoints

### Authentication
```
POST   /v1/auth/register     - Create new account
POST   /v1/auth/login        - Get access + refresh tokens
POST   /v1/auth/refresh      - Refresh access token
```

### Teams & Projects
```
POST   /v1/teams             - Create team
GET    /v1/teams             - List user's teams
POST   /v1/teams/{id}/members - Add team member
GET    /v1/projects          - List projects by team
POST   /v1/projects          - Create project
```

### Tasks
```
POST   /v1/tasks             - Create task
GET    /v1/tasks             - List tasks (paginated)
PATCH  /v1/tasks/{id}/status - Update status (triggers Kafka event)
POST   /v1/tasks/{id}/members - Assign user (triggers Kafka event)
```

### Real-Time Chat
```
WebSocket: ws://localhost:8080/api/ws

Subscribe: /topic/channel.{channelId}
Send:      /app/chat.{channelId}
```

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- PostgreSQL
- Kafka (or use Docker setup)

### Run Locally

```bash
# 1. Start dependencies (Kafka + PostgreSQL)
docker-compose up -d

# 2. Run the application
./mvnw spring-boot:run

# 3. Access Swagger UI
open http://localhost:8080/api/swagger-ui.html
```

### Test WebSocket
```
open http://localhost:8080/api/websocket-test.html
```

## Database Schema

```
Users ──┬── Teams (via TeamMember)
        ├── Projects (via ProjectMember)
        ├── Tasks (via TaskMember)
        ├── Channels (via ChannelMember)
        ├── DirectMessages
        └── Notifications

Teams ── Projects ── Tasks
      └── Channels ── Messages
```

[View Full DB Diagram](https://app.eraser.io/workspace/cKEDj34DX1uLgQPNa98N?origin=share)

## Configuration

Key properties in `application.properties`:

```properties
# JWT
jwt.secret=your-secret-key
jwt.access-token-expiration=900000      # 15 minutes
jwt.refresh-token-expiration=604800000  # 7 days

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# WebSocket
# Endpoint: /ws (with SockJS fallback)
```

## Project Structure

```
src/main/java/com/trelix/trelix_app/
├── config/          # Security, WebSocket, Kafka config
├── controller/      # REST + WebSocket controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── enums/           # Status, Role enums
├── exception/       # Custom exceptions + handlers
├── filter/          # JWT authentication filter
├── repository/      # Data access layer
├── service/         # Business logic
└── util/            # JWT utilities
```

## Author

Built with Spring Boot for demonstrating modern backend architecture patterns.
