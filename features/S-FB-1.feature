Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the temporal worker is active

  Scenario: Report defect and verify GitHub URL in Slack notification
    Given a defect report for "VW-454"
    When the defect is reported via the temporal worker
    Then the Slack body should contain a link to the GitHub issue