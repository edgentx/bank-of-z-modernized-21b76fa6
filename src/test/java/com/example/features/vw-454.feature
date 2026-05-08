Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect and verify GitHub link in Slack
    Given the Temporal workflow reports a defect via "temporal-worker exec"
    When the "activity" activity executes _report_defect
    Then the Slack message body should contain the GitHub issue URL