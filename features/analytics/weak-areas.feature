Feature: C2 - Recommend Weak Areas (Analytics Dashboard)
  As a user
  I want to receive recommendations for my weakest areas
  So that I know what to study next and improve efficiently

  Background:
    Given the API base URL is "http://localhost:8890"
    And the security mode is controlled by "spring.security.mode"
    And the endpoint under test is "GET /api/users/me/analytics/weak-areas"

  @C2 @openapi
  Scenario: OpenAPI exposes C2 endpoint
    Given the application is running
    When I request "GET /v3/api-docs"
    Then the response status should be 200
    And the OpenAPI paths should include "/api/users/me/analytics/weak-areas"

  @C2 @security @secure
  Scenario: Secure mode rejects unauthenticated access
    Given spring.security.mode is "secure"
    When I request "GET /api/users/me/analytics/weak-areas" without Authorization header
    Then the response status should be 401

  @C2 @security @dev
  Scenario: Dev mode allows access without authentication using fallback user
    Given spring.security.mode is "dev"
    When I request "GET /api/users/me/analytics/weak-areas" without Authorization header
    Then the response status should be 200
    And the response should be a JSON array

  @C2 @contract
  Scenario: Response items contain required fields
    Given spring.security.mode is "dev"
    And the user has category progress with weak areas
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And each item must contain:
      | categoryId              |
      | categoryName            |
      | currentAccuracy         |
      | targetAccuracy          |
      | accuracyGap             |
      | recommendedQuestions    |
      | recommendedDifficulty   |
      | estimatedTimeMinutes    |
      | priority                |

  @C2 @rules
  Scenario: Returns exactly top 3 weakest categories
    Given spring.security.mode is "dev"
    And the user has progress across 5 categories
    And at least 3 categories have sufficient attempts (minimum 5)
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And the response should contain at most 3 items
    And items should be sorted by currentAccuracy ascending

  @C2 @priority
  Scenario: Recommendations prioritize weakest accuracy first
    Given spring.security.mode is "dev"
    And the user has category progress where:
      | Category         | Attempts | Correct | Accuracy |
      | Speed Limits     | 10       | 4       | 40%      |
      | Priority Rules   | 10       | 7       | 70%      |
      | Traffic Signs    | 10       | 9       | 90%      |
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And the first recommendation should be "Speed Limits"
    And "Traffic Signs" should not be in the recommendations

  @C2 @min-attempts
  Scenario: Categories below minimum attempts are excluded
    Given spring.security.mode is "dev"
    And the user has category progress where:
      | Category       | Attempts | Correct | Accuracy |
      | Speed Limits   | 3        | 1       | 33%      |
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And the recommendations list should be empty

  @C2 @empty
  Scenario: New user receives empty recommendations
    Given spring.security.mode is "dev"
    And the user has no category progress records
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And the response should be an empty JSON array

  @C2 @strong-performance
  Scenario: User with only strong performance receives no weak areas
    Given spring.security.mode is "dev"
    And all user categories have accuracy >= 80%
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And the response should be an empty JSON array

  @C2 @target-accuracy
  Scenario: Target accuracy is set to 80% (Belgian standard)
    Given spring.security.mode is "dev"
    And the user has weak areas
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And each item should have targetAccuracy equal to 80.0

  @C2 @recommendations
  Scenario: Recommended questions calculated based on accuracy gap
    Given spring.security.mode is "dev"
    And the user has category with 40% accuracy
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And recommendedQuestions should be between 15 and 25

  @C2 @difficulty
  Scenario: Recommended difficulty based on current accuracy
    Given spring.security.mode is "dev"
    And the user has categories with varying accuracy
    When I request "GET /api/users/me/analytics/weak-areas"
    Then the response status should be 200
    And categories with accuracy < 70% should recommend "EASY"
    And categories with accuracy 70-80% should recommend "MEDIUM"
