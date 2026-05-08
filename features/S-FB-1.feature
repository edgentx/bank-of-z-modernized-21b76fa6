Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Report defect via Temporal and verify Slack notification
    Given the temporal worker is ready to report a defect
    When the client triggers _report_defect with ID "VW-454" and title "Validation Error"
    Then the Slack body should include the GitHub issue link
