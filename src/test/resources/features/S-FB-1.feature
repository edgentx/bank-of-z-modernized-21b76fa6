Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Developer
  I want defects reported via the Temporal worker to include a link to the created GitHub issue in the Slack notification
  So that the team can quickly triage the issue from the chat channel.

  Background:
    Given the Temporal worker is ready

  Scenario: Report defect and verify GitHub URL in Slack body
    Given a defect report is triggered with ID "VW-454"
    When the report_defect activity executes
    Then the Slack body contains the GitHub issue link
