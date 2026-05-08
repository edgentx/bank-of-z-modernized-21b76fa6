Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report Defect via Temporal includes GitHub URL
    Given the Slack adapter is initialized
    When the temporal worker executes "_report_defect" with issue "VW-454"
    Then the Slack body should contain the GitHub issue link