Feature: VW-454 Regression Validation

  Scenario: Verify GitHub URL inclusion in Slack notification
    Given a defect is reported with title "VW-454 Validation Failure"
    When the _report_defect workflow executes
    Then the Slack body contains the GitHub issue URL
