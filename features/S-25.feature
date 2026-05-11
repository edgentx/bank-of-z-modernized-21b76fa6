Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect tracking system is active

  Scenario: Valid GitHub URL is included in Slack notification body
    When the defect is reported via temporal-worker exec
    Then the Slack body includes GitHub issue: "https://github.com/example/vforce360/issues/454"

  Scenario: Invalid URL format is rejected
    When the defect is reported with an invalid URL
    Then validation fails preventing Slack notification
