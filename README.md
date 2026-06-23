# CollabEdit — Real-Time Collaborative Editor

> A production-deployed collaborative document editor where multiple users can type simultaneously and see each other's changes in real time. Built with Spring Boot, WebSocket (STOMP), Yjs CRDT, Redis pub/sub, and MongoDB Atlas.

**Live Demo:** https://collab-edit-aqum.onrender.com  
**GitHub:** https://github.com/vankayalakoutilya7/collab-editor

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.5, Spring WebSocket, STOMP |
| Real-time sync | Yjs (CRDT), SockJS, @stomp/stompjs |
| Message broker | Redis (Upstash cloud) — pub/sub for broadcasting |
| Database | MongoDB Atlas — document content persistence |
| Frontend | Vanilla HTML, CSS, JavaScript |
| Deployment | Render (Docker backend + Static Site frontend) |

---

## Features

- **Real-time collaborative editing** — multiple users edit simultaneously using Yjs CRDT which merges concurrent edits without conflicts
- **Document persistence** — auto-saved to MongoDB on every keystroke and reloaded on open
- **Any-room collaboration** — enter any document ID to create or join a session
- **Live word count** — tracks words, characters and lines in real time
- **Recent documents** — localStorage remembers last 5 opened documents
- **Share link** — one-click copy of a shareable URL with `?doc=docId`
- **Connection status** — live indicator in topbar (Connected / Connecting / Disconnected)

---

## Architecture

```
Browser (HTML + CSS + JS + Yjs)
        │
        │  SockJS + STOMP (WebSocket)
        │  REST (fetch API)
        ▼
Spring Boot Backend (Render — Docker)
        │                    │
        │ Redis pub/sub       │ Spring Data MongoDB
        ▼                    ▼
Upstash Redis           MongoDB Atlas
(broadcast updates)     (persist content)
```

### How real-time sync works

1. User types → Yjs encodes the change as a binary update
2. Frontend sends the update to `/app/edit/{docId}` via WebSocket
3. Backend publishes to Redis channel `document-updates`
4. Redis delivers to all subscribed backend instances
5. Backend broadcasts to `/topic/document/{docId}`
6. Each client receives and applies the Yjs update — CRDT guarantees no conflicts

### Why Redis?

Redis pub/sub acts as the message bus. Even with multiple backend instances running (horizontal scaling), every instance receives updates and forwards them to connected users.

---

## Project Structure

```
collab-editor-main/
├── collab-editor-backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/koutilya/collabeditor/
│       ├── config/
│       │   ├── WebSocketConfig.java    # STOMP endpoint, broker config
│       │   ├── CorsConfig.java         # CORS for REST endpoints
│       │   └── RedisConfig.java        # Upstash Redis connection + listener
│       ├── controller/
│       │   ├── DocumentController.java # WebSocket handlers + REST load endpoint
│       │   └── HealthController.java   # GET /health
│       ├── model/
│       │   └── Document.java           # MongoDB document model
│       ├── repository/
│       │   └── DocumentRepository.java # Spring Data MongoDB repo
│       └── service/
│           ├── DocumentService.java    # Save/load content from MongoDB
│           ├── RedisPublisher.java     # Publish Yjs updates to Redis
│           └── RedisSubscriber.java    # Receive from Redis → WebSocket broadcast
│
└── collab-editor-frontend/
    └── index.html                      # Full editor UI
```

---

## WebSocket Design

### Connection endpoint
```
/ws  (SockJS with HTTP fallback)
```

### Client to Server
| Destination | Payload | Purpose |
|---|---|---|
| `/app/edit/{docId}` | `List<Integer>` (Yjs bytes) | Send collaborative update |
| `/app/save/{docId}` | `String` (plain text) | Auto-save to MongoDB |

### Server to Client
| Topic | Purpose |
|---|---|
| `/topic/document/{docId}` | Broadcast edit to all collaborators in the document |

---

## REST API

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/health` | Health check |
| `GET` | `/document/{docId}` | Load saved content from MongoDB |

---

## Local Setup

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# 1. Clone
git clone https://github.com/vankayalakoutilya7/collab-editor.git
cd collab-editor/collab-editor-backend

# 2. Configure credentials in application.properties
spring.data.mongodb.uri=mongodb://localhost:27017/collabdb

# 3. Set Redis credentials in RedisConfig.java fallback values

# 4. Run
./mvnw spring-boot:run

# 5. Open — Spring Boot serves frontend automatically
http://localhost:8080
```

To test: open two browser tabs at `http://localhost:8080`, log in with different names and same document ID, and type in one tab.

---

## Deployment

### Backend (Render — Docker)

1. Push to GitHub
2. Create **Web Service** → connect repo
3. Root Directory: `collab-editor-backend`
4. Environment: `Docker`
5. Add environment variables:

| Variable | Value |
|---|---|
| `SPRING_DATA_MONGODB_URI` | MongoDB Atlas connection string |
| `SPRING_REDIS_HOST` | Upstash Redis host |
| `SPRING_REDIS_PORT` | `6379` |
| `SPRING_REDIS_PASSWORD` | Upstash Redis password |

### Frontend (Render — Static Site)

1. Create **Static Site** → same repo
2. Root Directory: `collab-editor-frontend`
3. Publish Directory: `.`
4. Update `BACKEND_URL` in `index.html` to your backend Render URL

### External Services

- **MongoDB Atlas** — cloud.mongodb.com — free M0 cluster
- **Upstash Redis** — console.upstash.com — free tier

---

## Key Technical Decisions

**Why Yjs CRDT?**
Yjs implements a CRDT that mathematically guarantees concurrent edits always converge to the same result without conflicts — no central server needed to order operations.

**Why Redis pub/sub?**
WebSocket connections are stateful — each client connects to one backend instance. Redis ensures that when a user on Instance 1 edits, the update reaches users on Instance 2. Without Redis, users on different instances would never see each other's changes.

**Why SockJS?**
SockJS automatically falls back to HTTP long-polling when WebSocket is blocked (corporate firewalls, proxies), making the app work in more environments without code changes.

**Why `@Controller` not `@RestController` for WebSocket handlers?**
Spring's `@MessageMapping` only works inside `@Controller`. Using `@RestController` silently breaks WebSocket message routing — the STOMP handshake completes but messages are never delivered to handler methods.

---

## Author

**Koutilya Vankayala**  
B.Tech — IIIT Vadodara  
github.com/vankayalakoutilya7

---

## License

MIT License — open source and free to use.
