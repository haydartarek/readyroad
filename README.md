# RijVia Backend

**Java 21 / Spring Boot API for the RijVia Belgian driving theory platform**

This repository contains the backend services that power RijVia: authentication, users, multilingual theory content, questions, categories, exam flows, progress data, administration, traffic-sign content, monitoring, and production-facing API behaviour.

**Live platform:** https://rijvia.be  
**Public API:** https://api.rijvia.be

---

## Table of contents

- [About the backend](#about-the-backend)
- [Responsibilities](#responsibilities)
- [Technology stack](#technology-stack)
- [Architecture](#architecture)
- [Core domains](#core-domains)
- [Authentication and authorization](#authentication-and-authorization)
- [Theory content and exam flows](#theory-content-and-exam-flows)
- [Administration](#administration)
- [Database and migrations](#database-and-migrations)
- [API documentation](#api-documentation)
- [Health and monitoring](#health-and-monitoring)
- [Testing](#testing)
- [Docker](#docker)
- [CI/CD](#cicd)
- [Local development](#local-development)
- [Environment configuration](#environment-configuration)
- [Data resources](#data-resources)
- [Project conventions](#project-conventions)
- [Security](#security)
- [Related repository](#related-repository)
- [Author and ownership](#author-and-ownership)

---

# About the backend

The RijVia backend is a production-oriented Spring Boot application serving the web and mobile clients.

It centralises business rules and persistent data so client applications do not need to duplicate important domain logic.

The backend is responsible for:

- account authentication and authorization;
- JWT-based protected API access;
- Google OAuth integration support;
- user and role management;
- theory questions and answer options;
- multilingual question and explanation content;
- category management;
- exam and practice lifecycle logic;
- progress and learning information;
- traffic-sign content;
- administration endpoints;
- notifications and mail-related flows;
- data validation;
- database migrations;
- production health checks;
- API documentation;
- automated backend tests.

---

# Responsibilities

RijVia separates frontend presentation from backend domain behaviour.

A typical request moves through the application as follows:

```text
Client request
     ↓
Controller
     ↓
Validation / Security
     ↓
Service layer
     ↓
Repository / persistence
     ↓
Database
```

This keeps exam rules, authorization, persistence, and administrative operations in one controlled service.

---

# Technology stack

## Runtime

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Web MVC**
- **Spring Security**
- **Spring Data JPA**
- **Bean Validation**
- **Spring Boot Actuator**

## Authentication

- JWT through `jjwt`
- Spring Security
- Google OAuth support

## Persistence

- JPA / Hibernate
- Flyway
- MySQL driver
- PostgreSQL driver
- H2 for test scenarios
- PostgreSQL Testcontainers support

## API and utilities

- Springdoc OpenAPI / Swagger UI
- Spring Mail
- Apache POI
- Google Auth libraries
- OpenAI Java client for backend functionality that requires it

## Build and tests

- Maven
- JUnit / Spring Boot Test
- Spring Security Test
- Mockito
- Testcontainers

## Deployment

- Docker
- Docker Compose configurations
- GitHub Actions
- production release tooling and health verification

---

# Architecture

A simplified repository structure:

```text
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/readyroad/...
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   └── resources/
│   └── test/
├── data/
├── public/images/signs/
├── deploy/
├── docs/
├── .github/workflows/
├── Dockerfile
├── docker-compose.yml
├── docker-compose.postgresql.yml
├── docker-compose.production-mirror.yml
├── pom.xml
└── README.md
```

Some internal Java package names and repository paths are historical technical identifiers retained for compatibility. The public product identity is **RijVia**.

---

# Core domains

## Users

The backend manages user-related data and secured account operations.

## Theory questions

Questions are represented as structured domain data rather than static frontend content.

A question can include:

- question text;
- answer options;
- correct-answer state;
- category;
- difficulty;
- multilingual content;
- explanations;
- active and publication state;
- optional image references;
- administrative metadata.

## Categories

Theory categories are centrally managed so the exam engine, practice flows, analytics, and administration use the same identifiers and data.

Technical category codes are internal identifiers. Client interfaces can resolve them to translated user-facing names.

## Traffic signs

Traffic-sign data is part of the learning content and can be exposed through dedicated APIs and administration functionality.

## Learning and progress

The backend supports learner progress and other learning-related information consumed by the client applications.

## Exam lifecycle

Exam operations are handled in the service layer so important behaviour remains server-controlled instead of relying on browser state alone.

---

# Authentication and authorization

The backend uses Spring Security and JWT-based authentication.

Protected endpoints are controlled by authenticated user state and role rules.

The application supports the roles required by the platform, including administrative access where needed.

Security-sensitive values such as JWT keys, database passwords, OAuth secrets, and SMTP passwords must come from environment configuration and must never be committed to Git.

---

# Theory content and exam flows

RijVia is built for Belgian Category B theory learning.

The platform works with multilingual theory content in:

- Arabic;
- Dutch;
- French;
- English.

The backend provides authoritative question and category data used by:

- topic practice;
- random practice;
- exam simulation;
- progress views;
- analytics;
- administration.

Question and category behaviour is validated by backend tests so important rules are not left to frontend assumptions.

---

# Administration

The backend exposes administrative operations used by the RijVia admin interface.

Administrative areas include functionality around:

- question-bank management;
- category creation and editing;
- category health and eligibility;
- question exposure and presentation statistics;
- exam history;
- traffic-sign content;
- users and roles;
- analytics and operational information.

Administrative authorization is enforced by the backend, not only by hiding frontend routes.

---

# Database and migrations

RijVia uses JPA-based persistence with Flyway migrations.

The project includes runtime support for both:

- MySQL;
- PostgreSQL.

The repository contains dedicated Docker Compose configurations for the default local stack, PostgreSQL development, and production-like validation.

The `.env.example` documents both database configurations.

## MySQL-style local variables

```env
DB_NAME=replace_with_database_name
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=replace_with_database_username
DB_PASSWORD=replace_with_secure_database_password
```

## PostgreSQL profile variables

```env
POSTGRES_DB=replace_with_postgresql_database
POSTGRES_HOST=localhost
POSTGRES_PORT=5433
POSTGRES_USERNAME=replace_with_postgresql_username
POSTGRES_PASSWORD=replace_with_secure_postgresql_password
POSTGRES_SSLMODE=disable
POSTGRES_SCHEMA=public
```

For hosted PostgreSQL deployments, SSL and connection settings must match the database provider requirements.

### Migration discipline

Schema changes should be introduced through Flyway migrations rather than direct manual edits to production tables.

This keeps database state reproducible across development, testing, staging-like environments, and production releases.

---

# API documentation

The backend includes Springdoc OpenAPI support.

When Swagger UI is enabled for the active environment, it can be available under a path such as:

```text
http://localhost:8080/swagger-ui/index.html
```

Exact exposure can depend on the selected environment and security configuration.

---

# Health and monitoring

Spring Boot Actuator provides application health information.

The production deployment pipeline verifies the public health endpoint:

```text
https://api.rijvia.be/actuator/health
```

A healthy production response is expected to report:

```json
{
  "status": "UP"
}
```

Health verification is part of the delivery path and not only a manual operational check.

---

# Testing

The backend CI runs both test and Maven verification phases.

## Run tests

```bash
mvn clean test
```

## Run Maven verification

```bash
mvn verify
```

## Package the application

```bash
mvn package
```

The project also includes Testcontainers support for PostgreSQL-backed test scenarios.

---

# Docker

The repository contains a production-oriented `Dockerfile` and multiple Compose configurations.

Typical local image build:

```bash
docker build -t rijvia-backend:local .
```

Existing deployment scripts can still use historical internal labels for compatibility. Those labels are implementation details and do not define public product branding.

---

# CI/CD

## Backend CI

GitHub Actions validates relevant backend changes through two main jobs.

### Verification job

```text
Checkout
   ↓
Java 21 setup
   ↓
mvn clean test
   ↓
mvn verify
   ↓
mvn package -DskipTests
```

### Docker job

After backend verification succeeds:

```text
Backend Docker image build
```

A failed verification stage prevents the backend CI pipeline from becoming successful.

## Production relationship

The production deployment workflow used by the client repository resolves a verified backend revision before creating a production release.

This means the production release path is designed around verified source revisions rather than arbitrary branch state.

The release model includes:

- verified backend and frontend revisions;
- controlled release creation;
- service health checks;
- public smoke verification;
- rollback-aware release handling.

---

# Local development

## Requirements

- Java 21
- Maven
- MySQL and/or PostgreSQL depending on the selected profile
- optional Docker / Docker Compose

## Clone

```bash
git clone https://github.com/haydartarek/readyroad.git
cd readyroad
```

## Configure environment

Copy the provided template:

```bash
cp .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

Replace every placeholder secret before startup.

## Run with Maven

```bash
mvn spring-boot:run
```

The active profile and environment determine database and integration behaviour.

## Run with Docker Compose

The repository includes multiple Compose files for different development and validation scenarios.

Review the selected Compose file and `.env.example` before startup instead of placing credentials directly into commands or committed configuration.

---

# Environment configuration

The environment template includes configuration for the following areas.

## Database

- MySQL connection values;
- PostgreSQL connection values;
- connection-pool limits;
- SSL mode;
- schema configuration.

## Authentication

```env
ADMIN_DEFAULT_PASSWORD=replace_with_secure_admin_password
JWT_SECRET=replace_with_long_random_base64_secret
```

## Frontend and CORS

```env
FRONTEND_URL=http://localhost:3000
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

## SMTP

```env
SMTP_HOST=replace_with_smtp_host
SMTP_PORT=587
SMTP_USERNAME=replace_with_smtp_username
SMTP_PASSWORD=replace_with_smtp_app_password
MAIL_FROM=info@rijvia.be
CONTACT_TO=info@rijvia.be
```

## Google OAuth

```env
GOOGLE_OAUTH_CLIENT_ID=replace_with_google_oauth_client_id
GOOGLE_OAUTH_CLIENT_SECRET=replace_with_google_oauth_client_secret
```

Real credentials must never be committed.

---

# Data resources

The build packages structured content resources used by the application, including category, lesson, and multilingual traffic-related data.

The Maven build explicitly includes selected data files in the application package so required runtime learning content is available after deployment.

---

# Project conventions

## Public identity

The product name is:

```text
RijVia
```

New public documentation, metadata, UI text, and product-facing assets should use the RijVia identity.

## Internal compatibility identifiers

Some internal artifact names, Java namespaces, repository names, or deployment labels are historical technical identifiers.

They should only be renamed when doing so is safe for:

- Java package compatibility;
- database migrations;
- Docker and deployment scripts;
- CI configuration;
- production rollback history.

A cosmetic rename must never be allowed to break production compatibility.

## Backend ownership of business rules

The backend remains the source of truth for security-sensitive behaviour and core business logic.

## Test discipline

Business-rule changes should include focused automated coverage.

---

# Security

Security requirements include:

- never commit passwords or API keys;
- keep JWT secrets outside source control;
- keep OAuth client secrets on the backend;
- restrict CORS to approved frontend origins;
- enforce authorization on backend endpoints;
- validate administrative access server-side;
- use environment secrets for SMTP and database access;
- rotate credentials if accidental exposure occurs;
- use health checks as part of release verification.

---

# Related repository

The client applications are maintained separately:

- **RijVia Web & Mobile:** https://github.com/haydartarek/readyroad_front_end

Additional links:

- **Live platform:** https://rijvia.be
- **Public API:** https://api.rijvia.be
- **GitHub:** https://github.com/haydartarek
- **LinkedIn:** https://www.linkedin.com/in/haydartarek-dev/
- **Portfolio:** https://haydartarek.github.io/portfolio_site/

---

# Author and ownership

RijVia is designed, developed, maintained, and operated by **Haydar Tarek**.

**Author:** Haydar Tarek  
**GitHub:** https://github.com/haydartarek  
**GitHub email:** 94225472+haydartarek@users.noreply.github.com  
**Project contact:** info@rijvia.be  
**LinkedIn:** https://www.linkedin.com/in/haydartarek-dev/  
**Portfolio:** https://haydartarek.github.io/portfolio_site/

---

## Status

The RijVia backend is actively maintained and is used by the live platform. Backend changes are validated through automated tests and Docker verification before they are considered suitable for production use.
