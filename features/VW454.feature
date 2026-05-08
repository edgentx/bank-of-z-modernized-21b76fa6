Feature: VW-454 Regression — GitHub URL in Slack body

  As a VForce360 Engineer
  I want to ensure that defect reports include the GitHub issue URL in the Slack notification body
  So that developers can quickly navigate to the issue from the alert

  Scenario: Verify Slack body contains GitHub URL for reported defect
    Given a defect report for VW-454 is triggered
    When the temporal worker executes the _report_defect workflow
    Then the Slack body includes the GitHub issue link
