Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Temporal worker reports defect and generates Slack body with GitHub URL
    Given a defect is reported via temporal-worker exec
    When the report_defect workflow completes
    Then the Slack body contains GitHub issue link
