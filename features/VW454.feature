Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Reporting a defect should result in a Slack notification with a GitHub link
    Given the temporal worker executes the defect reporting workflow
    When _report_defect is triggered via temporal-worker exec
    Then the Slack body contains the GitHub issue URL
