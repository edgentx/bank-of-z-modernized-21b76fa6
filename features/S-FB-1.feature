Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports to include the GitHub issue URL in the Slack notification
  So that I can quickly navigate to the issue from the #vforce360-issues channel

  Scenario: Triggering defect report includes GitHub URL in notification
    Given a defect report with ID "VW-454" and GitHub URL "https://github.com/example/repo/issues/454"
    When the _report_defect command is executed via temporal-worker
    Then the Slack notification body must contain the GitHub URL "https://github.com/example/repo/issues/454"
