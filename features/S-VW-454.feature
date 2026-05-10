Feature: GitHub URL in Slack body (End-to-End Regression)

  Scenario: Temporal worker reports defect and verifies GitHub link presence
    Given a defect report is triggered via temporal-worker exec
    When the system processes the defect report command
    Then the resulting event payload contains the GitHub issue link
