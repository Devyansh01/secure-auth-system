# 🔐 Secure User Authentication System

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-BCrypt-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

A **production-ready REST API** for secure user registration and login, built with Spring Boot and Spring Security. Implements enterprise-grade BCrypt password hashing, layered architecture, robust input validation, and clean exception handling.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Architecture](#-architecture)
- [Security Design](#-security-design)
- [API Endpoints](#-api-endpoints)
- [Request & Response Examples](#-request--response-examples)
- [Setup & Run](#-setup--run)
- [Validation Rules](#-validation-rules)
- [Error Handling](#-error-handling)
- [What I Learned](#-what-i-learned)

---

## 📖 Overview

This project implements a **stateless, secure authentication backend** from scratch using core Spring Security architecture — no third-party auth libraries, no shortcuts. Every design decision prioritizes security, maintainability, and real-world production standards.

**Key Highlights:**
- 🔒 Passwords hashed with **BCrypt (strength 12)** — never stored in plain text
- 🏗️ Strict **Controller → Service → Repository** layered architecture
- ✅ Field-level **input validation** with descriptive error messages
- 🚨 **Global exception handling** for clean, consistent JSON error responses
- 🔄 **Stateless session** design (no HttpSession, ready for JWT integration)
- 🧪 **Unit tested** with Mockito — registration, login, and failure scenarios

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 + BCrypt |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Testing | JUnit 5 + Mockito |
| API Testing | Postman |

---

## 📁 Project Structure

```
src/
├── main/java/com/authsystem/
│   ├── AuthSystemApplication.java       # Entry point
│   ├── config/
│   │   └── SecurityConfig.java          # BCrypt bean, filter chain, route rules
│   ├── controller/
│   │   └── AuthController.java          # REST endpoints
│   ├── dto/
│   │   ├── RegisterRequest.java         # Incoming registration payload + validation
│   │   ├── LoginRequest.java            # Incoming login payload
│   │   └── AuthResponse.java            # Outgoing response structure
│   ├── entity/
│   │   └── User.java                    # JPA entity → maps to 'users' table
│   ├── exception/
│   │   ├── UserAlreadyExistsException.java
│   │   └── GlobalExceptionHandler.java  # Intercepts all exceptions → clean JSON errors
│   ├── repository/
│   │   └── UserRepository.java          # Spring Data JPA queries
│   └── service/
│       ├── AuthService.java             # Core business logic
│       └── CustomUserDetailsService.java # Bridge: DB User → Spring Security UserDetails
└── test/java/com/authsystem/
    └── service/
        └── AuthServiceTest.java         # Unit tests with Mockito
```

---

## 🏗 Architecture

```
Client (Postman / Frontend)
         │
         ▼
   [ HTTP Request ]
         │
         ▼
┌─────────────────────┐
│  Spring Security     │  ← Checks if route is public or protected
│  Filter Chain        │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│   AuthController    │  ← Receives request, runs @Valid, calls service
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│    AuthService      │  ← Business logic: duplicate checks, BCrypt, auth
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  UserRepository     │  ← Spring Data JPA → MySQL queries
└─────────────────────┘
```

---

## 🔒 Security Design

| Concern | Implementation |
|---|---|
| Password Storage | BCrypt hash (strength 12, ~250ms per hash) |
| Plain-text exposure | Never stored, never logged, never returned in responses |
| Duplicate users | Application-level check (409 Conflict) before DB insert |
| Input sanitization | Regex validation on all incoming fields |
| Session management | Stateless — no HttpSession created |
| Error messages | Generic on auth failure (never reveals which field was wrong) |
| CSRF | Disabled (standard for stateless REST APIs) |
| Route protection | `/api/auth/**` public, all other routes require authentication |

---

## 📡 API Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Authenticate and login |
| `GET` | `/api/auth/health` | Public | Service health check |

---

## 📨 Request & Response Examples

### ✅ Register — Success

**Request:**
```json
POST /api/auth/register
Content-Type: application/json

{
  "username": "devyansh01",
  "email": "devyansh@example.com",
  "password": "Secure@123"
}
```

**Response — 201 Created:**
```json
{
  "success": true,
  "message": "Registration successful",
  "username": "devyansh01",
  "email": "devyansh@example.com",
  "role": "ROLE_USER"
}
```

---

### ✅ Login — Success

**Request:**
```json
POST /api/auth/login
Content-Type: application/json

{
  "username": "devyansh01",
  "password": "Secure@123"
}
```

**Response — 200 OK:**
```json
{
  "success": true,
  "message": "Login successful",
  "username": "devyansh01",
  "email": "devyansh@example.com",
  "role": "ROLE_USER"
}
```

---

### ❌ Register — Validation Failure

**Response — 400 Bad Request:**
```json
{
  "timestamp": "2025-08-01T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "details": {
    "password": "Password must contain uppercase, lowercase, digit, and special character",
    "email": "Must be a valid email address"
  }
}
```

---

### ❌ Register — Duplicate User

**Response — 409 Conflict:**
```json
{
  "timestamp": "2025-08-01T10:31:00",
  "status": 409,
  "error": "Username 'devyansh01' is already taken"
}
```

---

### ❌ Login — Wrong Credentials

**Response — 401 Unauthorized:**
```json
{
  "timestamp": "2025-08-01T10:32:00",
  "status": 401,
  "error": "Invalid username or password"
}
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running locally

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/Devyansh01/secure-auth-system.git
cd secure-auth-system
```

**2. Create the MySQL database**
```sql
CREATE DATABASE auth_db;
```

**3. Configure your credentials**

Open `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

**4. Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

Server starts at → `http://localhost:8080`

> Hibernate auto-creates the `users` table on first run. No manual SQL needed.

**5. Test with Postman**

Import and hit the endpoints:
- `POST http://localhost:8080/api/auth/register`
- `POST http://localhost:8080/api/auth/login`
- `GET  http://localhost:8080/api/auth/health`

---

## ✅ Validation Rules

### Username
| Rule | Detail |
|---|---|
| Required | Cannot be blank |
| Length | 3 to 50 characters |
| Pattern | Letters, numbers, underscores only (`^[a-zA-Z0-9_]+$`) |

### Email
| Rule | Detail |
|---|---|
| Required | Cannot be blank |
| Format | Must be valid email format |
| Max Length | 100 characters |

### Password
| Rule | Detail |
|---|---|
| Required | Cannot be blank |
| Length | 8 to 72 characters (72 = BCrypt max) |
| Uppercase | At least one uppercase letter |
| Lowercase | At least one lowercase letter |
| Digit | At least one number |
| Special char | At least one of `@$!%*?&` |

---

## 🚨 Error Handling

All errors return a **consistent JSON structure** — no stack traces, no HTML, no cryptic messages:

| Scenario | HTTP Status |
|---|---|
| Validation failure | `400 Bad Request` |
| Duplicate username/email | `409 Conflict` |
| Wrong credentials | `401 Unauthorized` |
| Unexpected server error | `500 Internal Server Error` |

Handled by `GlobalExceptionHandler.java` using `@RestControllerAdvice` — intercepts exceptions from all controllers in one place.

---

## 💡 What I Learned

- How **Spring Security's filter chain** processes every request before it reaches the controller
- How **BCrypt's work factor** balances security strength vs. hashing performance
- Why **DTOs must be separated from Entities** — security, flexibility, and API contract control
- How `AuthenticationManager` internally calls `UserDetailsService` + BCrypt comparison — without manual password checking
- Why **stateless session design** enables horizontal scaling in REST APIs
- How `@RestControllerAdvice` centralises exception handling across the entire application
- Why **Optional<>** is the correct return type for repository queries that may find no result

---

## 🔮 Planned Enhancements

- [ ] JWT token generation on login
- [ ] Refresh token with DB persistence
- [ ] Email verification on registration
- [ ] Login attempt rate limiting (bucket4j)
- [ ] Role-based access control (`ROLE_ADMIN` protected routes)
- [ ] Docker containerization

---

## 👨‍💻 Author

**Devyansh Namdev**  
Java Backend Developer | Spring Boot | REST APIs | MySQL

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=flat&logo=linkedin)](https://linkedin.com/in/devyansh-namdev-889711277)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=flat&logo=github)](https://github.com/Devyansh01)
[![Email](https://img.shields.io/badge/Email-Contact-D14836?style=flat&logo=gmail)](mailto:namdev.devyansh01@gmail.com)

---

> ⭐ If this project helped you understand Spring Security or authentication concepts, consider giving it a star!
