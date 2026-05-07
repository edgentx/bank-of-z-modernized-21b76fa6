Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports posted to Slack to include the GitHub issue URL
  So that I can quickly navigate from the alert to the issue tracking system.

  Scenario: Verify defect report includes GitHub URL
    Given a defect report is generated for VW-454
    When the defect is published to the Slack channel
    Then the Slack body should include the GitHub issue link
