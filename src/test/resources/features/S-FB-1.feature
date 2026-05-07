Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Engineer
  I want to ensure that defect reports include valid GitHub links
  So that issues are traceable in Slack

  Scenario: Verify Slack body contains GitHub issue link
    Given a defect report command exists for VW-454
    When the defect is reported via temporal-worker exec
    Then the Slack body should contain the GitHub issue URL
    And the validation should pass without errors
