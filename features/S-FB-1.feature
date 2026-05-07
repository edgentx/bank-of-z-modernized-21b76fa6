Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the temporal worker is initialized with mock adapters

  Scenario: Verify defect reporting generates valid URL and Slack notification
    Given the defect "VW-454" is reported via temporal-worker exec
    When the VForce360 system generates a GitHub issue URL
    And the Slack notification is triggered
    Then the Slack body should include the GitHub issue URL
