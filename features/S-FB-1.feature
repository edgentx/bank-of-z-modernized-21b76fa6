Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports triggered via Temporal to include a clickable GitHub link in the Slack notification
  So that I can quickly navigate to the issue from the alert channel

  Background: 
    Given the VForce360 PM diagnostic system is operational

  Scenario: Verify defect reporting includes GitHub URL
    When _report_defect is triggered via temporal-worker exec for issue "https://github.com/org/repo/issues/454"
    Then the Slack body includes GitHub issue "https://github.com/org/repo/issues/454"
