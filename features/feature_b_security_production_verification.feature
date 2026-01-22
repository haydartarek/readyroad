Feature: Feature B Security Verification (Dev vs Production)

  As a platform maintainer
  I want to clearly distinguish between dev fallback security and production security
  So that Feature B endpoints are safe in production and convenient in development

  Background:
    Given the ReadyRoad backend application is available
    And Feature B endpoints are implemented
    And the application uses profile-based security configuration

  # ------------------------------------------------------------------
  # Scenario 1: Application must run on the correct port
  # ------------------------------------------------------------------
  Scenario: Application is running on the expected port
    Given the application is started
    When I check the application logs
    Then the embedded Tomcat server should be running on port 8890
    And any request sent to port 8080 should fail

  # ------------------------------------------------------------------
  # Scenario 2: OpenAPI exposes all Feature B endpoints
  # ------------------------------------------------------------------
  Scenario: OpenAPI documentation exposes Feature B endpoints
    Given the application is running on port 8890
    When I request GET /v3/api-docs
    Then the response status should be 200
    And the OpenAPI paths should include "/api/quiz/questions/{questionId}/answer"
    And the OpenAPI paths should include "/api/users/me/progress/overall"
    And the OpenAPI paths should include "/api/users/me/progress/categories"

  # ------------------------------------------------------------------
  # Scenario 3: Production security blocks unauthenticated access
  # ------------------------------------------------------------------
  Scenario: Unauthenticated access is rejected in secure mode
    Given the application is started with profile "secure"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/overall
    Then the response status should be 401 or 403

    When I request GET /api/users/me/progress/categories
    Then the response status should be 401 or 403

    When I POST /api/quiz/questions/1/answer with a valid body
    Then the response status should be 401 or 403

  # ------------------------------------------------------------------
  # Scenario 4: Dev fallback allows access without authentication
  # ------------------------------------------------------------------
  Scenario: Dev fallback is active in dev mode
    Given the application is started with profile "dev"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/overall
    Then the response status should be 200
    And the response should represent data for fallback user ID 1
    And the logs should contain "[DEV MODE]"

  # ------------------------------------------------------------------
  # Scenario 5: Test profile does not prove production security
  # ------------------------------------------------------------------
  Scenario: Test profile uses dev fallback by design
    Given the application is running under the "test" profile
    And spring.security.mode is set to "dev"
    When all integration tests are executed
    Then the tests should pass without authentication
    But production security behavior is not yet proven

  # ------------------------------------------------------------------
  # Scenario 6: Secure mode must be provable by at least one test
  # ------------------------------------------------------------------
  Scenario: Secure mode behavior is explicitly verified
    Given spring.security.mode is overridden to "secure"
    When I run FeatureBProductionSecurityTest without authentication
    Then the test should verify 401 Unauthorized responses
    And this test should serve as proof of production security

  # ------------------------------------------------------------------
  # Review conclusion
  # ------------------------------------------------------------------
  Scenario: Feature B security verification is considered complete
    Given manual verification confirms secure mode returns 401
    And automated test FeatureBProductionSecurityTest verifies secure behavior
    Then Feature B security may be marked as "Verified"
    And the dev fallback must remain feature-flagged
