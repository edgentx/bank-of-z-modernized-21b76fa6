Feature: Defect Reporting Integration

  Scenario: Validating VW-454 — GitHub URL in Slack body
    Given the defect reporting temporal worker is initialized
    When _report_defect is triggered with title "Login Failure" and details "Auth service returns 500"
    Then the Slack body should include the GitHub issue link
