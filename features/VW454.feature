Feature: Validate VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect reporting system is initialized

  Scenario: Verify Slack notification includes GitHub issue link
    When the temporal worker executes _report_defect workflow
    Then the Slack body should contain the GitHub issue URL
