Feature: Validating VW-454 — GitHub URL in Slack body

  As a developer or support engineer
  I want defect reports posted to Slack to contain a link to the GitHub issue
  So that I can quickly navigate to the issue to view details or start working on it.

  Background:
    Given the system is configured with mock adapters for testing

  Scenario: Trigger defect report and verify GitHub link in Slack body
    Given a defect report command is issued for VW-454
    And the GitHub service returns issue URL "https://github.com/mock-org/issues/1"
    When the temporal worker executes the defect report workflow
    Then the Slack body includes the GitHub issue link "https://github.com/mock-org/issues/1"
