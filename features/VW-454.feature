Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect reporting system is initialized

  Scenario: Report defect via Temporal and verify Slack contains GitHub URL
    When the temporal worker executes the report_defect workflow for VW-454
    Then the Slack notification body should contain the GitHub issue URL
