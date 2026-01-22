Feature: Sprint 4 - Belgian Compliance Gates (Stories D3 & D4)
  As a platform maintainer
  I want to enforce Belgian compliance at publish time
  So that only legally valid questions can be used in exams

  Background:
    Given the platform is running in secure mode
    And Belgian compliance is active
    And traffic signs exist in the system
    And categories are configured

  # ==========================================
  # Story D3: Traffic Sign Integration
  # ==========================================

  @D3 @traffic-sign @compliance
  Scenario: Cannot publish question without traffic sign
    Given a draft question exists with all required fields
    But the question has no traffic sign reference
    When an admin attempts to publish the question
    Then the system refuses publication
    And the error message explains "traffic sign" is required
    And the error message mentions "legal context"

  @D3 @traffic-sign @compliance
  Scenario: Can publish question with traffic sign
    Given a draft question exists with all required fields
    And the question references a valid traffic sign
    When an admin publishes the question
    Then the question status becomes PUBLISHED
    And publishedAt timestamp is set
    And the traffic sign linkage is persisted

  @D3 @traffic-sign @stability
  Scenario: Traffic sign linkage is stable after publish
    Given a question is published with traffic sign A1
    When the question is retrieved multiple times
    Then the same traffic sign A1 appears every time
    And the linkage is deterministic

  # ==========================================
  # Story D4: Content Validation Gates
  # ==========================================

  @D4 @validation @publish-gate
  Scenario: Publishing validates full Belgian compliance
    Given a draft question is missing NL translation
    When an admin attempts to publish the question
    Then the system refuses publication
    And the error message mentions "NL" translation

  @D4 @validation @options
  Scenario: Cannot publish question with 4 options
    Given a draft question has 4 answer options
    When an admin attempts to publish the question
    Then the system refuses publication
    And the error message mentions "2-3 options" requirement

  @D4 @validation @options
  Scenario: Cannot publish question with 1 option
    Given a draft question has 1 answer option
    When an admin attempts to publish the question
    Then the system refuses publication
    And the error message mentions "2-3 options" requirement

  @D4 @immutability
  Scenario: Published questions cannot be republished
    Given a question is already published
    When an admin attempts to publish it again
    Then the system refuses with "already published" error

  @D4 @validation @dry-run
  Scenario: canPublish returns false for invalid questions
    Given a draft question lacks required traffic sign
    When canPublish is called
    Then it returns false
    And no database changes occur

  @D4 @validation @dry-run
  Scenario: canPublish returns true for valid questions
    Given a draft question meets all compliance requirements
    When canPublish is called
    Then it returns true

  @D4 @validation @errors
  Scenario: getPublishValidationErrors returns detailed errors
    Given a draft question has multiple compliance violations
    And it lacks traffic sign reference
    And it lacks NL translation
    When getPublishValidationErrors is called
    Then at least 2 error messages are returned
    And each error is descriptive

  # ==========================================
  # System Truths
  # ==========================================

  @system-truth @compliance
  Scenario: Users never see illegal questions
    Given exam and practice modes are active
    When questions are selected for delivery
    Then every delivered question is compliant by construction
    And no runtime patching is required

  @system-truth @domain-authority
  Scenario: Compliance is enforced by the domain not by UI
    Given requests come from any client
    When validation is triggered
    Then the backend is the final authority
    And UI cannot bypass domain rules
