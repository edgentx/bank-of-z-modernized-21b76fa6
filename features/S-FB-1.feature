Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the validation worker is running
    And the Slack notifier is mocked
    And the GitHub issue tracker is mocked

  Scenario: Report defect generates GitHub URL and sends to Slack
    When a defect "VW-454" is reported with severity "LOW"
    Then a GitHub issue should be created
    And the Slack message body should contain the GitHub issue URL
    And the message should be posted to "#vforce360-issues"

  Scenario: Regression test for missing URL
    When a defect is reported
    Then the Slack body must NOT be empty
    And the Slack body must start with "http"
