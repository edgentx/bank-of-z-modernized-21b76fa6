Feature: Defect Reporting - GitHub URL Validation

  Scenario: Report defect via Temporal and verify Slack notification
    Given a defect reporting system is available
    When the temporal worker triggers report_defect
    Then the Slack body contains the GitHub issue link
