Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 developer
  I want to ensure that defect reports include the GitHub issue link in the Slack notification
  So that the team can click through to the issue directly from the alert

  Scenario: Report defect via Temporal Worker
    Given the defect reporting system is initialized
    When the temporal worker triggers "VW-454" with url "https://github.com/org/repo/issues/1"
    Then the Slack body should include the GitHub issue link
