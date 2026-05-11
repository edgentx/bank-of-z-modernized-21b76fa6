Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify GitHub link is present in Slack notification when defect is reported
    Given a defect report is triggered via temporal-worker exec
    When the defect report workflow executes with details "VW-454 Validation", "Body text", "LOW"
    Then the Slack body should contain the GitHub issue link