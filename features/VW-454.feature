Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Trigger report_defect and verify Slack body contains GitHub URL
    Given the defect reporting workflow is triggered
    When the report_defect activity executes
    Then the Slack body contains the GitHub issue URL
