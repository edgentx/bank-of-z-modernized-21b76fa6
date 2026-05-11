Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect reporting system is initialized

  Scenario: Valid defect report posts GitHub URL to Slack
    Given a valid defect report command with URL "https://github.com/egdcrypto/bank-of-z/issues/454"
    When the report defect command is executed
    Then Slack should receive a notification containing the GitHub issue link

  Scenario: Invalid URL should not trigger Slack notification
    Given a valid defect report command with URL "not-a-url"
    When the report defect command is executed
    Then Slack should not receive any notification
