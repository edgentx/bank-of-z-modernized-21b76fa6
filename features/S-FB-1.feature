Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect report includes GitHub link in Slack notification
    Given the temporal worker executes the _report_defect workflow
    When the defect payload is processed
    Then the Slack body includes the GitHub issue link "https://github.com/example/bank-of-z-modernization/issues/454"
