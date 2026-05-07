Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify GitHub URL is passed to Slack notification
    Given the system is ready to report defects
    When the defect report is triggered with title "VW-454 Defect" and body "Reproduction steps..."
    Then the Slack notification body should contain the GitHub issue URL
    And the Slack notification body should not be null
