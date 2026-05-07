Feature: Validating VW-454 GitHub URL in Slack body (end-to-end)

  Scenario: Verify defect report includes GitHub link in Slack notification
    Given a defect report is triggered via temporal-worker exec
    When the system processes the defect reporting command
    Then the Slack body contains the GitHub issue URL
