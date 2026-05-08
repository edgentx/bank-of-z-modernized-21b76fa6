Feature: Defect Reporting Integration

  Scenario: Validating VW-454 GitHub URL in Slack body
    Given the defect VW-454 exists
    When the temporal worker executes _report_defect for VW-454
    Then the Slack body includes the GitHub issue URL
