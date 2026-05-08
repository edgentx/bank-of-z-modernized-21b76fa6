Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect includes GitHub link in Slack notification
    Given the defect reporting workflow is initialized
    When the temporal worker executes the report_defect workflow
    Then the Slack body includes the GitHub issue URL
