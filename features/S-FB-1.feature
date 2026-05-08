Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Triggering report_defect ensures Slack body contains GitHub link
    Given the defect reporting service is initialized
    When I trigger _report_defect via temporal-worker exec with severity "LOW" and component "validation"
    Then the Slack body contains the GitHub issue link
