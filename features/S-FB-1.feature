Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports to include the GitHub issue link in the Slack notification body
  So that I can quickly navigate to the issue from the chat log

  Scenario: Triggering defect report includes GitHub URL in Slack
    Given the defect VW-454 is triggered via temporal-worker exec
    When the report_defect command is executed
    Then the Slack body should contain the GitHub issue link
    And the validation no longer exhibits the reported behavior
