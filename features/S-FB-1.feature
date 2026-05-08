Feature: Validate VW-454 — GitHub URL in Slack body

  Scenario: Verify GitHub URL is present in Slack body after defect reporting
    Given the temporal worker is running
    When _report_defect is triggered with details "Login fails intermittently"
    Then the Slack body should contain GitHub issue link
