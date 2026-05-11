Feature: VForce360 Defect Reporting Validation

  Scenario: Verify Slack body contains GitHub issue link for VW-454
    Given a defect report for VW-454 is prepared
    When the temporal worker executes _report_defect command
    Then the Slack message body must contain the GitHub issue link
