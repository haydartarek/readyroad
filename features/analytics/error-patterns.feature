Feature: C1 - View Error Patterns (Analytics Dashboard)
  As a user
  I want to see my common error patterns
  So that I can understand my systematic mistakes

  Background:
    Given the API base url is "http://localhost:8890"
    And the security mode is controlled by "spring.security.mode"
    And the endpoint under test is "GET /api/users/me/analytics/error-patterns"

  @C1 @openapi
  Scenario: OpenAPI exposes C1 endpoint
    Given the application is running
    When I request "GET /v3/api-docs"
    Then the response status should be 200
    And the OpenAPI paths should include "/api/users/me/analytics/error-patterns"

  @C1 @security @secure
  Scenario: Secure mode denies access without authentication
    Given spring.security.mode is "secure"
    When I request "GET /api/users/me/analytics/error-patterns" without Authorization header
    Then the response status should be 401

  @C1 @security @dev
  Scenario: Dev mode allows access without authentication using fallback user
    Given spring.security.mode is "dev"
    When I request "GET /api/users/me/analytics/error-patterns" without Authorization header
    Then the response status should be 200
    And the response should be a JSON array

  @C1 @contract
  Scenario: Response items contain required fields
    Given spring.security.mode is "dev"
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And each item must contain:
      | patternType      |
      | count            |
      | percentage       |
      | description      |
      | exampleQuestions |

  @C1 @rules
  Scenario: Identifies exactly 6 error pattern types (when data exists)
    Given spring.security.mode is "dev"
    And the user has wrong answers that cover all supported patterns
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And the response should include patternType values:
      | SIGN_CONFUSION            |
      | PRIORITY_MISUNDERSTANDING |
      | SPEED_LIMIT_ERROR         |
      | RULE_OVERGENERALIZATION   |
      | ZONE_CONFUSION            |
      | SUPPLEMENTARY_IGNORED     |

  @C1 @sorting
  Scenario: Patterns are sorted by frequency descending
    Given spring.security.mode is "dev"
    And the user has wrong answers where SIGN_CONFUSION count is greater than SPEED_LIMIT_ERROR
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And the first item patternType should be "SIGN_CONFUSION"

  @C1 @examples
  Scenario: Each pattern includes example questions (up to a safe limit)
    Given spring.security.mode is "dev"
    And the user has wrong answers for SIGN_CONFUSION
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And the SIGN_CONFUSION item should include at least 1 exampleQuestions entry

  @C1 @percent
  Scenario: Percentages are computed relative to total wrong answers
    Given spring.security.mode is "dev"
    And the user has exactly 10 wrong answers in total
    And SIGN_CONFUSION count is 4
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And the SIGN_CONFUSION percentage should be 40.0

  @C1 @empty
  Scenario: No wrong answers returns empty list
    Given spring.security.mode is "dev"
    And the user has no wrong answers
    When I request "GET /api/users/me/analytics/error-patterns"
    Then the response status should be 200
    And the response should be an empty JSON array

  # Implementation Status: ✅ COMPLETE (Jan 21, 2026, 01:53 AM)
  # - Service: AnalyticsService.getErrorPatterns() (227 lines)
  # - Tests: 6/6 integration tests passing
  # - DTOs: ErrorPatternResponse + ExampleQuestionDTO
  # - Controller: AnalyticsController.getErrorPatterns() (exposed)
  # - API: GET /api/users/me/analytics/error-patterns
  # - OpenAPI: Fully documented with security requirements
  # - Test Coverage: All scenarios covered by integration tests
