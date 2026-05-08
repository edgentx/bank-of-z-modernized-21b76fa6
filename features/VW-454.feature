Feature: GitHub URL in Slack body (end-to-end)

  Scenario: Verify defect report includes GitHub link in Slack notification
    Given a defect is reported via temporal-worker exec
    When the workflow processes the report
    Then Slack body contains GitHub issue link
