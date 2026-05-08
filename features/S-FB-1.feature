Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the mock adapters are initialized

  Scenario: Report defect and verify GitHub URL is present in Slack notification
    Given the defect "VW-454" has a GitHub issue URL "https://github.com/example/bank-of-z/issues/454"
    When the defect "VW-454" is reported via temporal-worker exec
    Then the Slack body should contain the GitHub issue link
    And the Slack body should not be empty

  Scenario: Report defect with different ID
    Given the defect "VW-999" has a GitHub issue URL "https://github.com/example/bank-of-z/issues/999"
    When the defect "VW-999" is reported via temporal-worker exec
    Then the Slack body should contain the GitHub issue link
