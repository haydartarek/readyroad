Feature: Feature B Endpoint Security & Authentication Verification

  As a ReadyRoad API consumer
  I want proper authentication and authorization for Feature B endpoints
  So that user data is protected and production security standards are met

  Background:
    Given the ReadyRoad API is running on http://localhost:8890
    And OpenAPI documentation is available at /v3/api-docs
    And the application supports two security modes:
      | mode   | behavior                                           |
      | dev    | Falls back to test user ID 1 (spring.security.mode=dev) |
      | secure | Requires JWT authentication (spring.security.mode=secure) |

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 1: OpenAPI Documentation
  # ═══════════════════════════════════════════════════════════════════════

  @openapi
  Scenario: OpenAPI exposes all Feature B endpoints
    When I request GET /v3/api-docs
    Then the response status should be 200
    And the response content-type should be "application/json"
    And paths should include:
      | endpoint                                    |
      | /api/quiz/questions/{questionId}/answer     |
      | /api/users/me/progress/overall              |
      | /api/users/me/progress/categories           |

  @openapi
  Scenario: OpenAPI documents security requirements
    When I request GET /v3/api-docs
    And I parse the OpenAPI JSON
    Then endpoint "/api/quiz/questions/{questionId}/answer" should document:
      | response | description                    |
      | 200      | Answer submitted successfully  |
      | 400      | Invalid request                |
      | 401      | User not authenticated         |
      | 404      | Question not found             |
    And endpoint "/api/users/me/progress/overall" should document:
      | response | description                    |
      | 200      | Progress retrieved successfully|
      | 401      | User not authenticated         |

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 2: Dev Mode Behavior (spring.security.mode=dev)
  # ═══════════════════════════════════════════════════════════════════════

  @dev-mode @security
  Scenario: Dev mode - Unauthenticated requests use fallback user ID 1
    Given spring.security.mode is set to "dev"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/overall
    Then the response status should be 200
    And the response should contain progress data for user ID 1
    And the server logs should contain "[DEV MODE] No authentication found, using fallback user ID 1"

  @dev-mode @security
  Scenario: Dev mode - Submit answer without authentication
    Given spring.security.mode is set to "dev"
    And no Authorization header is provided
    And question with ID 1 exists
    When I POST /api/quiz/questions/1/answer with valid body
    Then the response status should be 200
    And the answer should be stored for user ID 1
    And the server logs should contain "[DEV MODE]"

  @dev-mode @security
  Scenario: Dev mode - Category progress without authentication
    Given spring.security.mode is set to "dev"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/categories
    Then the response status should be 200
    And the response should contain category data for user ID 1

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 3: Production Mode Behavior (spring.security.mode=secure)
  # ═══════════════════════════════════════════════════════════════════════

  @production @security
  Scenario: Production mode - Unauthenticated request to overall progress
    Given spring.security.mode is set to "secure"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/overall
    Then the response status should be 401
    And the response body should be empty
    And the server logs should contain "[PRODUCTION MODE] No authentication provided - access denied"

  @production @security
  Scenario: Production mode - Unauthenticated request to category progress
    Given spring.security.mode is set to "secure"
    And no Authorization header is provided
    When I request GET /api/users/me/progress/categories
    Then the response status should be 401
    And the response body should be empty

  @production @security
  Scenario: Production mode - Unauthenticated answer submission
    Given spring.security.mode is set to "secure"
    And no Authorization header is provided
    And question with ID 1 exists
    When I POST /api/quiz/questions/1/answer with valid body
    Then the response status should be 401
    And the answer should NOT be stored
    And the server logs should contain "Unauthenticated access attempt"

  @production @security
  Scenario: Production mode - Anonymous authentication is rejected
    Given spring.security.mode is set to "secure"
    And authentication principal is "anonymousUser"
    When I request GET /api/users/me/progress/overall
    Then the response status should be 401
    And the server logs should contain "[PRODUCTION MODE] No authentication provided"

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 4: Authenticated Requests with JWT
  # ═══════════════════════════════════════════════════════════════════════

  @authenticated @jwt
  Scenario: Authenticated user views overall progress
    Given spring.security.mode is set to "secure"
    And I am authenticated as user 888 with a valid JWT token
    And user 888 has submitted 20 practice answers (15 correct)
    When I request GET /api/users/me/progress/overall with Authorization header
    Then the response status should be 200
    And the response should include:
      | field              | value |
      | totalAttempted     | 20    |
      | totalCorrect       | 15    |
      | overallAccuracy    | 75.00 |
    And the server logs should contain "[PRODUCTION MODE] Authenticated user: <username> (ID: 888)"

  @authenticated @jwt
  Scenario: Authenticated user views category progress
    Given spring.security.mode is set to "secure"
    And I am authenticated as user 888 with a valid JWT token
    And user 888 has activity in 3 categories
    When I request GET /api/users/me/progress/categories with Authorization header
    Then the response status should be 200
    And the response should be an array of 3 category progress items
    And each item should contain categoryId, categoryName, accuracyRate

  @authenticated @jwt
  Scenario: Authenticated user submits an answer
    Given spring.security.mode is set to "secure"
    And I am authenticated as user 888 with a valid JWT token
    And question with ID 1 exists with 3 options
    When I POST /api/quiz/questions/1/answer with valid body and JWT
    Then the response status should be 200
    And the response should contain:
      | field        | type    |
      | isCorrect    | boolean |
      | questionId   | number  |
      | correctOptionId | number |
    And the answer should be stored for user 888
    And user 888's progress metrics should be updated

  @authenticated @jwt
  Scenario: JWT contains user principal with ID
    Given spring.security.mode is set to "secure"
    And I have a valid JWT token for user with username "testuser" and ID 777
    When I request any Feature B endpoint with this JWT
    Then AuthenticationUtil should extract user ID as 777
    And the request should be processed for user 777
    And NOT for fallback user 1

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 5: User Isolation & Security
  # ═══════════════════════════════════════════════════════════════════════

  @security @isolation
  Scenario: User can only access their own progress via /me endpoint
    Given I am authenticated as user 888
    And user 999 exists with different data
    When I request GET /api/users/me/progress/overall
    Then the response should contain data for user 888 only
    And user 999's data should NOT be accessible
    And there is no way to query user 999's data via this endpoint

  @security @isolation
  Scenario: Progress service filters by authenticated user ID
    Given I am authenticated as user 888
    When ProgressService.getOverallProgress() is called
    Then the service receives userId parameter as 888
    And all database queries filter by WHERE user_id = 888
    And no cross-user data is returned

  @security @isolation
  Scenario: Answer submission is tied to authenticated user
    Given I am authenticated as user 888
    When I submit an answer to question 1
    Then the answer is stored with user_id = 888
    And user 888's progress is updated
    And no other user's data is affected

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 6: AuthenticationUtil Behavior
  # ═══════════════════════════════════════════════════════════════════════

  @component @authentication-util
  Scenario Outline: AuthenticationUtil extracts user ID based on security mode
    Given spring.security.mode is "<mode>"
    And authentication is <auth_state>
    When AuthenticationUtil.extractUserId() is called
    Then the returned user ID should be <result>

    Examples:
      | mode   | auth_state              | result           |
      | dev    | null                    | 1 (fallback)     |
      | dev    | anonymousUser           | 1 (fallback)     |
      | dev    | User(id=888)            | 888 (extracted)  |
      | secure | null                    | null (denied)    |
      | secure | anonymousUser           | null (denied)    |
      | secure | User(id=888)            | 888 (extracted)  |

  @component @authentication-util
  Scenario: AuthenticationUtil logs appropriate warnings
    Given spring.security.mode is "dev"
    And no authentication is provided
    When AuthenticationUtil.extractUserId() is called
    Then the log should contain "[DEV MODE] No authentication found, using fallback user ID 1"
    And the log level should be WARN

  @component @authentication-util
  Scenario: AuthenticationUtil in production mode logs security events
    Given spring.security.mode is "secure"
    And no authentication is provided
    When AuthenticationUtil.extractUserId() is called
    Then the log should contain "[PRODUCTION MODE] No authentication provided - access denied"
    And the returned value should be null

  @component @authentication-util
  Scenario: AuthenticationUtil extracts User from principal
    Given spring.security.mode is "secure"
    And authentication.principal is a User object with ID 777
    When AuthenticationUtil.extractUserId() is called
    Then user.getId() should be called
    And the returned value should be 777

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 7: Edge Cases & Error Handling
  # ═══════════════════════════════════════════════════════════════════════

  @edge-case @security
  Scenario: Invalid JWT token in production mode
    Given spring.security.mode is set to "secure"
    And I provide an invalid or expired JWT token
    When I request GET /api/users/me/progress/overall
    Then the response status should be 401
    Because JWT filter rejects the token before reaching controller

  @edge-case @security
  Scenario: Malformed Authorization header
    Given spring.security.mode is set to "secure"
    And I provide Authorization header "Bearer malformed-token"
    When I request GET /api/users/me/progress/overall
    Then the response status should be 401
    And the error should indicate invalid authentication

  @edge-case @security
  Scenario: Unknown authentication principal type in production
    Given spring.security.mode is set to "secure"
    And authentication.principal is a String (not User object)
    When AuthenticationUtil.extractUserId() is called
    Then the log should contain "Invalid authentication principal type"
    And the returned value should be null
    And the controller should return 401

  @edge-case @dev-mode
  Scenario: Unknown authentication principal type in dev mode
    Given spring.security.mode is set to "dev"
    And authentication.principal is a String
    When AuthenticationUtil.extractUserId() is called
    Then the log should contain "Authentication principal type unknown, using fallback user ID 1"
    And the returned value should be 1
    And the request should succeed

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 8: Configuration Verification
  # ═══════════════════════════════════════════════════════════════════════

  @configuration
  Scenario: Dev profile sets security mode to dev
    Given application is started with profile "dev"
    When I check spring.security.mode property
    Then it should be set to "dev"
    And AuthenticationUtil.isDevMode() should return true
    And AuthenticationUtil.isAuthenticationRequired() should return false

  @configuration
  Scenario: Secure profile sets security mode to secure
    Given application is started with profile "secure"
    When I check spring.security.mode property
    Then it should be set to "secure" (or default value)
    And AuthenticationUtil.isDevMode() should return false
    And AuthenticationUtil.isAuthenticationRequired() should return true

  @configuration
  Scenario: Default security mode is secure (production-safe)
    Given no spring.security.mode is configured
    When AuthenticationUtil reads the property
    Then it should default to "secure"
    And authentication should be required by default

  # ═══════════════════════════════════════════════════════════════════════
  # Scenario Group 9: Integration with Existing Tests
  # ═══════════════════════════════════════════════════════════════════════

  @integration @existing-tests
  Scenario: Existing integration tests work with dev mode
    Given integration tests run with @ActiveProfiles("test")
    And test profile sets spring.security.mode to "dev"
    When PracticeAnswerSubmissionIntegrationTest runs
    Then all 8 tests should pass
    Because dev mode allows unauthenticated access

  @integration @existing-tests
  Scenario: Existing tests use fallback user ID 1
    Given integration tests don't provide authentication
    And test profile uses dev mode
    When tests call Feature B endpoints
    Then requests are processed for user ID 1
    And test data is isolated to user 1

  # ═══════════════════════════════════════════════════════════════════════
  # Implementation Notes
  # ═══════════════════════════════════════════════════════════════════════

  # Components Created:
  # 1. AuthenticationUtil.java
  #    - Profile-based authentication extraction
  #    - Dev mode: fallback to user ID 1
  #    - Production: strict JWT authentication
  #    - Logging for security events
  #
  # 2. Updated Controllers:
  #    - ProgressController (B2, B3)
  #    - QuizController (B1)
  #    - Both now use AuthenticationUtil
  #    - Return 401 when userId is null (production)
  #
  # 3. Configuration:
  #    - application-dev.yml: spring.security.mode=dev
  #    - application-secure.yml: spring.security.mode=secure
  #    - Default: secure (production-safe)

  # Security Model:
  # - Dev Mode: Relaxed for testing (fallback user 1)
  # - Production: Strict JWT requirement
  # - User Isolation: /api/users/me/progress design
  # - No Cross-User Access: Repository-level filtering

  # Test Coverage:
  # - All existing tests pass (dev mode)
  # - New security scenarios documented
  # - Production mode behavior defined
  # - JWT integration prepared
