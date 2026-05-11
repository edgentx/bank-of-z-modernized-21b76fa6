Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect report should include GitHub link in Slack notification
    Given the defect reporting system is initialized
    When a defect report is triggered with valid data
    Then the Slack body includes the GitHub issue URL
