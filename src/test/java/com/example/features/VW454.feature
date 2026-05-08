Feature: VW-454 Regression Test
  As a VForce360 Engineer
  I want to ensure defect reports always contain valid GitHub URLs
  So that issues are traceable in the GitHub repository.

  Scenario: Validating Slack body with a correct GitHub URL
    Given a defect report body containing a valid GitHub URL
    When the temporal worker executes the report_defect workflow validation
    Then the validation should pass

  Scenario: Rejecting Slack body without GitHub URL (Defect Case)
    Given a defect report body missing the GitHub URL
    When the temporal worker executes the report_defect workflow validation
    Then the validation should fail indicating the missing URL

  Scenario: Rejecting Slack body with malformed URL
    Given a defect report body containing a malformed URL
    When the temporal worker executes the report_defect workflow validation
    Then the validation should fail indicating the malformed URL
