Feature: Defect Reporting End-to-End (S-FB-1)

  Scenario: Verify GitHub URL appears in Slack notification
    Given the system is ready to report defects
    When the temporal worker executes _report_defect
    Then the Slack body contains the GitHub issue link
