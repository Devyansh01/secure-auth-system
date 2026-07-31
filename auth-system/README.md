# Secure User Authentication System

A production-ready REST API for user registration and login built with Spring Boot, Spring Security, and BCrypt.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + BCrypt (strength 12) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| Validation | Jakarta Bean Validation |
| Build | Maven |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8 running locally

---

## Setup

### 1. Create the MySQL database

```sql
CREATE DATABASE auth_db;
```

### 2. Configure credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

### 3. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

The server starts on `http://localhost:8080`. Hibernate will auto-create the `users` table on first run.

---

## API Endpoints

### POST `/api/auth/register`

Registers a new user.

**Request body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Secure@123"
}
```

**Password rules:** min 8 chars, must include uppercase, lowercase, digit, and special character.

**201 Created:**
```json
{
  "success": true,
  "message": "Registration successful",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "ROLE_USER"
}
```

**409 Conflict** — username or email already taken.  
**400 Bad Request** — validation errors with field-level details.

---

### POST `/api/auth/login`

Authenticates a user.

**Request body:**
```json
{
  "username": "john_doe",
  "password": "Secure@123"
}
```

**200 OK:**
```json
{
  "success": true,
  "message": "Login successful",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "ROLE_USER"
}
```

**401 Unauthorized** — invalid credentials.

---

### GET `/api/auth/health`

Returns `200 OK` with `"Auth service is running"`.

---

## Security Design

- Passwords are **never stored in plain text** — BCrypt with strength 12 is applied before persistence.
- Spring Security's `AuthenticationManager` handles login — raw passwords are never compared manually.
- Sessions are **stateless** (no cookies). Integrate JWT for token-based auth as a next step.
- CSRF protection is disabled (appropriate for stateless REST APIs).
- All public routes are limited to `/api/auth/register` and `/api/auth/login`.

---

## Running Tests

```bash
mvn test
```

---

## Project Structure

```
src/
├── main/java/com/authsystem/
│   ├── AuthSystemApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   └── AuthResponse.java
│   ├── entity/
│   │   └── User.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── UserAlreadyExistsException.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── service/
│       ├── AuthService.java
│       └── CustomUserDetailsService.java
└── test/java/com/authsystem/
    └── service/
        └── AuthServiceTest.java
```
