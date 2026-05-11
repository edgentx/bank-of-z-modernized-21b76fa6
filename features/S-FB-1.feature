Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the Slack adapter is initialized
    And the GitHub client is initialized

  Scenario: Validating GitHub URL presence in Slack notification
    Given a defect report VW-454 exists
    When the system executes the _report_defect workflow via Temporal worker
    Then the Slack body contains the GitHub issue link
