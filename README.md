# SnipLink — URL Shortener & Analytics Dashboard

A modern, high-performance, full-stack URL shortening service and analytics engine built with **Java (Spring Boot 3)**, **Vue 3**, and **MySQL / H2**.

Designed with clean service boundaries and production-ready conventions so you can easily dockerize it yourself.

---

## 🏗️ Architecture Overview

The system is organized into decoupled services:

```
url-shortener/
├── backend/          # Java 21+ & Spring Boot 3.3.4 REST API & Redirect Engine
├── frontend/         # Vue 3 (Composition API) + Vite Single Page Application
└── README.md         # Architecture, local run, and Docker containerization guide
```

### 1. Backend Service (`backend/`)
- **Framework**: Spring Boot 3.3.4, Spring Data JPA, Spring Web.
- **Port**: Default `8080` (configurable via `SERVER_PORT`).
- **Database Profiles**:
  - `h2` (default): In-memory database with zero installation required for local testing. Includes H2 console at `/h2-console`.
  - `mysql`: Production profile configured for MySQL / MariaDB via `application-mysql.yml`.
- **Key Capabilities**:
  - Base62 unique short code generation or custom user aliases.
  - Lightning-fast HTTP 302 redirection at `GET /{shortCode}`.
  - Asynchronous / transactional click tracking capturing:
    - User-Agent parsed into Browser, OS, and Device category.
    - Client IP address (`X-Forwarded-For` or remote socket).
    - Traffic referrer source.
    - Timestamp history.
  - Seeded with sample links (`/spring`, `/vuejs`, `/docker`) on fresh start.

### 2. Frontend Service (`frontend/`)
- **Framework**: Vue 3 (`<script setup>`) + Vite.
- **Port**: Default `5173` (configured with `host: true` for container environments).
- **Features**:
  - Clean SaaS dashboard with dark theme and responsive tables.
  - Short link creation form with instant copy-to-clipboard.
  - Real-time Analytics Modal:
    - Total Clicks, Unique Referrers, Top Browser, Top OS.
    - Visual percentage breakdown bars for Browsers, Operating Systems, and Traffic Sources.
    - Recent click log showing timestamp, IP, User-Agent, and Referrer.

---

## 🚀 Running Locally (Without Docker)

### Prerequisites
- Java 21 or 23 (`java -version`)
- Maven (`mvn -v`)
- Node.js (v18+) & npm

### 1. Run Backend (uses H2 in-memory DB out of the box)
```bash
cd backend

# Compile and start with Maven (defaults to H2 in-memory profile)
mvn spring-boot:run
```
The API and redirection engine will be running at `http://localhost:8080`.
- Health check: `http://localhost:8080/api/health`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:urlshortener`, username: `sa`, password: blank)

### 2. Run Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start Vite dev server
npm run dev
```
Open your browser at `http://localhost:5173`.

---

## 📡 REST API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/{shortCode}` | Redirects (302) to original URL and tracks click analytics |
| `POST` | `/api/urls` | Create short URL (`{ "originalUrl": "...", "customCode": "...", "title": "..." }`) |
| `GET` | `/api/urls` | List all shortened URLs with click counters |
| `GET` | `/api/urls/{shortCode}/analytics` | Detailed analytics breakdown and recent click log |
| `DELETE` | `/api/urls/{id}` | Delete a short URL and associated analytics |
| `GET` | `/api/health` | Service health status check |

---

## 🐳 Containerization Guide (For Your Docker Setup)

Since you are containerizing this application yourself, here are the architectural specifications and Dockerfile recommendations:

### 1. Environment Variables Matrix

| Service | Variable | Default (Local) | Suggested (Docker Network) |
| :--- | :--- | :--- | :--- |
| **Backend** | `SERVER_PORT` | `8080` | `8080` |
| **Backend** | `SPRING_PROFILES_ACTIVE` | `h2` | `mysql` |
| **Backend** | `DB_HOST` | `localhost` | `mysql` (Docker service name) |
| **Backend** | `DB_PORT` | `3306` | `3306` |
| **Backend** | `DB_NAME` | `urlshortener` | `urlshortener` |
| **Backend** | `DB_USER` | `root` | `root` |
| **Backend** | `DB_PASSWORD` | `rootpassword` | `rootpassword` |
| **Backend** | `APP_BASE_URL` | `http://localhost:8080` | `http://localhost:8080` (public redirect base URL) |
| **Backend** | `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | `http://localhost:5173,http://localhost:80` |
| **Frontend** | `VITE_API_URL` | `http://localhost:8080` | `http://localhost:8080` (browser-facing backend URL) |

### 2. Recommended Dockerfile Strategies

#### **Backend (`backend/Dockerfile`) — Multi-stage Build**:
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/url-shortener-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **Frontend (`frontend/Dockerfile`) — Multi-stage Nginx Build**:
```dockerfile
# Stage 1: Build Vue app
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve via Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### **Database (`mysql`)**:
- Image: `mysql:8.0`
- Environment:
  - `MYSQL_ROOT_PASSWORD=rootpassword`
  - `MYSQL_DATABASE=urlshortener`
- Named volume: `mysql-data:/var/lib/mysql`

### 3. Docker Compose Networking Tips
- Connect all services to a shared network (e.g. `app-net`).
- In Compose, specify `depends_on: { mysql: { condition: service_healthy } }` for the backend so Spring Boot waits for MySQL to be ready before initializing Hibernate.
- Remember: `VITE_API_URL` is executed by the client's web browser, so it should be mapped to the host port (e.g., `http://localhost:8080`).
# SnipLink
