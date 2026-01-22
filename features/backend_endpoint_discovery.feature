Feature: Backend endpoint discovery and API documentation
  As a developer
  I want to verify all backend endpoints are correctly configured
  So that the API is accessible and well-documented

  Background:
    Given the ReadyRoad backend is running on port 8890
    And the health endpoint responds with HTTP 200 at "/api/health"

  Scenario: Validate that documented controllers are reachable
    When I send GET requests to the known controller base paths
    Then each existing endpoint should return HTTP 200 or a meaningful HTTP status not 404
    And any wrong path like "/api/trafficsigns" should return HTTP 404

  Scenario Outline: Test all correct API endpoints
    Given the backend is running
    When I send a GET request to "<endpoint>"
    Then the response status code should be <status>
    And the response should be valid

    Examples:
      | endpoint                              | status |
      | /api/health                           | 200    |
      | /api/categories                       | 200    |
      | /api/traffic-signs                    | 200    |
      | /api/lessons                          | 200    |
      | /api/exam-questions                   | 200    |
      | /api/exam-questions/random            | 200    |
      | /api/practice-questions/lesson/1      | 200    |
      | /swagger-ui.html                      | 200    |
      | /swagger-ui/index.html                | 200    |
      | /v3/api-docs                          | 200    |
      | /actuator/health                      | 200    |
      | /actuator/info                        | 200    |

  Scenario Outline: Test wrong paths return 404
    Given the backend is running
    When I send a GET request to "<wrong_endpoint>"
    Then the response status code should be 404
    And the error message should indicate resource not found

    Examples:
      | wrong_endpoint       |
      | /api/courses         |
      | /api/signs           |
      | /api/trafficsigns    |
      | /                    |
      | /api/wrong-path      |

  Scenario: Enable Swagger UI for API exploration
    Given the project includes springdoc-openapi dependency
    When I restart the application
    Then "/v3/api-docs" should return HTTP 200 with valid OpenAPI JSON
    And "/swagger-ui/index.html" should load successfully
    And all controllers should be visible in Swagger UI

  Scenario: Verify API naming conventions
    Given the backend follows RESTful naming conventions
    Then all endpoints should use kebab-case for multi-word resources
    And the traffic signs endpoint should be "/api/traffic-signs" not "/api/trafficsigns"
    And the lessons endpoint should be "/api/lessons" not "/api/courses"

  Scenario: Verify Actuator endpoints
    Given Spring Boot Actuator is enabled
    When I access "/actuator/health"
    Then the response should contain status "UP"
    And the response should be JSON format

  Scenario: Verify security configuration allows public access
    Given the security configuration permits all requests
    When I access any API endpoint without authentication
    Then the request should succeed
    And no authentication should be required in development mode

  Scenario: Comprehensive endpoint test
    Given the application is fully started
    When I test the following endpoints in sequence:
      | Endpoint Type       | URL                                    |
      | Health Check        | /api/health                            |
      | Categories          | /api/categories                        |
      | Traffic Signs       | /api/traffic-signs                     |
      | Lessons             | /api/lessons                           |
      | Exam Questions      | /api/exam-questions/random?limit=5     |
      | Practice Questions  | /api/practice-questions/lesson/1       |
      | Swagger UI          | /swagger-ui.html                       |
      | OpenAPI Spec        | /v3/api-docs                           |
      | Actuator Health     | /actuator/health                       |
    Then all requests should succeed with HTTP 200
    And the responses should be valid JSON (except HTML for Swagger UI)
