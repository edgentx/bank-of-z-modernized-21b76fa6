Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect via Temporal Worker and verify GitHub link in Slack
    Given a defect report is triggered
    When the Temporal worker executes "_report_defect"
    Then the Slack body contains the GitHub issue link
