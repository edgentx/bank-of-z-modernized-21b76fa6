Feature: Defect Reporting Integration

  Scenario: Verify Slack body contains GitHub URL (VW-454)
    Given a defect report is triggered for VW-454
    When the report_defect workflow executes
    Then the Slack body includes the GitHub issue link
