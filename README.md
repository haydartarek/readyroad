# ReadyRoad Backend

ReadyRoad Backend is a Java Spring Boot API for a road assistance and traffic-learning platform. It is designed as the backend service for the ReadyRoad front-end application.

The project demonstrates backend development with Spring Boot, REST APIs, security, database integration, validation, documentation, testing, and production-oriented configuration.

## Related Repository

- Front-end: https://github.com/haydartarek/readyroad_front_end
- Portfolio: https://haydartarek.github.io/portfolio_site/

## Main Features

- REST API backend built with Spring Boot
- User authentication and authorization with Spring Security
- JWT-based security flow
- Database persistence with Spring Data JPA
- MySQL integration
- Database migration support with Flyway
- Input validation
- API documentation with OpenAPI / Swagger UI
- Mail support
- Actuator support for monitoring
- Unit and integration testing setup
- Cucumber BDD testing support

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Bean Validation

### Database
- MySQL
- H2 for testing
- Flyway migrations

### Documentation and Testing
- OpenAPI / Swagger UI
- JUnit
- Spring Boot Test
- Spring Security Test
- Cucumber BDD
- Mockito

### Tools
- Maven
- Lombok
- Git and GitHub

## Architecture Overview

```text
readyroad/
├── src/main/java/com/readyroad
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   ├── service/
│   └── ReadyroadApplication.java
├── src/main/resources/
├── src/test/
├── data/
├── pom.xml
└── README.md
```

The exact structure may evolve as the project grows.

## Getting Started

### Prerequisites

- Java 21
- Maven
- MySQL

### Clone the repository

```bash
git clone https://github.com/haydartarek/readyroad.git
cd readyroad
```

### Configure the database

Create a MySQL database and configure the application properties with your local credentials.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/readyroad
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run the application

```bash
mvn spring-boot:run
```

### Run tests

Run unit tests:

```bash
mvn test
```

Run integration tests:

```bash
mvn verify
```

## API Documentation

When the application is running, Swagger UI can be available at:

```text
http://localhost:8080/swagger-ui.html
```

or:

```text
http://localhost:8080/swagger-ui/index.html
```

## Security

The backend uses Spring Security and JWT dependencies to support secure authentication and protected API access.

Security-related configuration should never expose secrets, API keys, passwords, or production credentials in the repository.

## Project Value

This project demonstrates practical backend skills required for Java developer roles:

- Building RESTful APIs
- Structuring a Spring Boot application
- Working with database persistence
- Applying authentication and authorization
- Writing tests
- Preparing documentation
- Connecting backend services with a front-end application

## Future Improvements

- Add Docker support
- Add CI/CD workflow with GitHub Actions
- Add a public environment example file
- Improve API documentation examples
- Add deployment instructions
- Add more integration tests

## Author

Haydar Tarek

- GitHub: https://github.com/haydartarek
- LinkedIn: https://www.linkedin.com/in/haydartarek-dev/
- Portfolio: https://haydartarek.github.io/portfolio_site/
