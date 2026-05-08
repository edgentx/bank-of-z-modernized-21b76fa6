Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports to include valid GitHub links in Slack notifications
  So that I can quickly jump to the ticket to investigate the issue.

  Scenario: Trigger defect report and verify Slack content
    Given a defect report is triggered for issue VW-454
    When the temporal worker executes the defect reporting workflow
    Then the Slack body includes the GitHub issue URL
    And the Slack body does not contain placeholder text
