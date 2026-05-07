Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given a defect report command for VW-454

  Scenario: Valid defect report contains GitHub URL in Slack body
    When the command is executed with a valid GitHub URL
    Then the resulting event should contain the GitHub URL in the Slack body

  Scenario: Invalid URL is rejected
    When the command is executed with a malformed URL
    Then validation should fail with an error
