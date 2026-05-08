Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify defect reporting includes GitHub link in Slack notification
    Given the notification system is initialized
    When the defect VW-454 is reported with title "Fix GitHub URL" and description "Missing link in Slack body"
    Then the Slack body should contain the GitHub issue link
    And the Slack body should include the issue title
