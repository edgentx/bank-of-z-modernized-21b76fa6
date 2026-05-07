Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify GitHub URL is included in Slack notification
    Given a defect report is triggered via temporal-worker exec
    When the system processes the report_defect command
    Then the Slack body contains GitHub issue link
