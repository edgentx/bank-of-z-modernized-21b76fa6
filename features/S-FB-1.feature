Feature: S-FB-1 - Fix: Validating VW-454 - GitHub URL in Slack body (end-to-end)

  As a VForce360 system
  I want defect reports to include a link to the GitHub issue in Slack notifications
  So that developers can quickly navigate from a Slack alert to the corresponding issue

  Background:
    Given a defect reporting service is available
    And the Slack channel "#vforce360-issues" is configured for defect reports

  Scenario: Successfully report a defect with GitHub link in Slack notification
    Given a defect with ID "VW-454", title "Validating VW-454 - GitHub URL in Slack body" and description "Slack body should include GitHub issue URL"
    When the defect is reported via temporal-worker exec
    Then the Slack body should contain the GitHub issue link
    And the GitHub issue should be created
    And the Slack message should be sent to the correct channel

  Scenario: Report defect but GitHub API fails
    Given a defect with ID "VW-455", title "GitHub API failure test" and description "Testing GitHub API failure handling"
    And the GitHub API is unavailable
    When the defect is reported via temporal-worker exec
    Then the defect report should fail
    And no Slack message should be sent

  Scenario: Report defect but Slack API fails
    Given a defect with ID "VW-456", title "Slack API failure test" and description "Testing Slack API failure handling"
    And the Slack API is unavailable
    When the defect is reported via temporal-worker exec
    Then the defect report should fail
    And the GitHub issue should still be created
