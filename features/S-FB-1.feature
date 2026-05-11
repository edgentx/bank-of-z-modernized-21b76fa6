Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Trigger report_defect via temporal-worker exec and verify Slack body
    Given a defect is reported via temporal-worker exec
    When the defect report is processed
    Then the system generates a GitHub issue link
    And the Slack body contains the GitHub issue link
    And validation no longer exhibits the reported behavior
