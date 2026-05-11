Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify defect report contains GitHub URL
    Given a defect report is triggered via temporal-worker exec
    When the report_defect workflow completes
    Then the Slack body should include the GitHub issue URL
