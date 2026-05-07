Feature: Defect Reporting Validation (S-FB-1)

  Scenario: Verifying GitHub URL in Slack body (VW-454)
    Given the temporal worker triggers defect reporting
    When _report_defect creates a GitHub issue
    Then the Slack body should contain the GitHub issue URL
