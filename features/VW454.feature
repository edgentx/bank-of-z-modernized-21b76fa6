Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Trigger defect report and verify Slack body
    Given a defect is reported via temporal-worker exec
    When the workflow processes the report
    Then the Slack body contains GitHub issue link