Feature: VW-454 Regression Test
  As a system user
  I want defect reports to include the GitHub issue link in the Slack notification
  So that I can click through to the issue directly from Slack

  Scenario: Verify Slack body contains GitHub URL after defect report
    Given a defect report is triggered via temporal-worker exec
    Then the Slack body should include the GitHub issue link
