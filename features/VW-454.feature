Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify Slack notification includes GitHub link after defect reporting
    Given the temporal worker is initialized
    When _report_defect is triggered with valid data
    Then the Slack body contains the GitHub issue link
