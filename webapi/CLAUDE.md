# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Unit tests only (default profile)
./mvnw clean test

# Integration tests only (requires Docker for MySQL TestContainers)
./mvnw clean verify -P integration-test

# All tests
./mvnw clean verify -P ci-server

# Run a single test class
./mvnw test -Dtest=MealControllerTest

# Run a single test method
./mvnw test -Dtest=MealControllerTest#testMethodName

# Run the application locally (starts MySQL in Docker)
./local-env/app.sh

# Build production Docker image
./mvnw clean package -P prod
```

## Architecture Overview

This is a Spring Boot 4 REST API for meal tracking with JWT authentication.

**Layered Architecture:**

- `api/rest/` - REST controllers with request/response handling
- `services/` - Business logic layer
- `repositories/` - Spring Data JPA repositories
- `domains/` - JPA entities (User, Meal)
- `security/jwt/` - JWT token generation and validation
- `payloads/` - DTOs and response envelopes (SuccessEnvelop, ErrorEnvelop)
- `exceptions/` - Custom exception hierarchy with GlobalExceptionHandler

**Key Patterns:**

- All API responses wrapped in `SuccessEnvelop` or `ErrorEnvelop`
- Soft delete pattern (entities have `deleted` boolean flag)
- Role-based access: REGULAR_USER, USER_MANAGER, ADMIN
- Privileges: MY_MEALS, USER_MANAGEMENT, MEAL_MANAGEMENT
- `@CurrentUser` annotation injects authenticated UserPrincipal into controllers
- `@Secured` annotation for method-level authorization

**Test Organization:**

- `/src/test/java/` - Unit tests with @WebMvcTest and mocked services
- `/src/integration-test/java/` - Integration tests with TestContainers MySQL
- Test data generators in `com.mealtracker.utils` (UserGenerator, MealGenerator)

**Database:**

- MySQL with Flyway migrations in `src/main/resources/db/migration/`
- Application runs on port 9000 with context path `/api`

**Security:**

- JWT tokens with HS512 algorithm, 7-day expiration
- Public endpoints: POST `/v1/sessions` (login), POST `/v1/users` (register), GET `/actuator/health`
