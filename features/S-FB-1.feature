Feature: VForce360 Regression Tests
  As a System Engineer
  I want to ensure defect reports are valid
  So that links in Slack notifications work correctly

  Scenario: Verify GitHub URL in Slack body for defect report
    Given a defect report is triggered via temporal-worker exec
    When the defect processing completes
    Then the Slack body contains GitHub issue link
