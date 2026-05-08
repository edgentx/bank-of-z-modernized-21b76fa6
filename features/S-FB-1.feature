Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given a defect report request for VW-454 is triggered

  Scenario: Verify defect reporting generates a valid GitHub URL
    When the temporal worker executes the report_defect command
    Then the Slack body contains the GitHub issue link
