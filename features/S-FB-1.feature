Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the Temporal worker is initialized for defect reporting

  Scenario: Verify defect report includes GitHub URL
    When the defect report workflow "VW-454" is triggered via Temporal exec
    Then the Slack body contains the GitHub issue URL
    And the message is sent to the correct channel
