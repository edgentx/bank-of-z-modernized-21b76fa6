Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the validation domain is initialized

  Scenario: Verify GitHub URL is included in Slack notification
    When a defect report is triggered for VW-454
    And the temporal worker executes the validation logic
    Then the Slack body contains the GitHub issue link
