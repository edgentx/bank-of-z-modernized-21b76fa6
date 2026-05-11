Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  @S-FB-1
  Scenario: Verify Slack body includes GitHub URL
    Given a defect is reported via Temporal worker
    When the defect report workflow executes to completion
    Then the Slack notification body contains the GitHub issue URL