Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Trigger defect report and verify Slack body contains GitHub link
    Given the defect reporting system is initialized
    When the temporal worker executes the defect report for VW-454
    Then the Slack body should contain the GitHub issue link
    And the validation no longer exhibits the reported behavior
