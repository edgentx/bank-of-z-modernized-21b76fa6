Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub issue link
    Given a defect is reported via VForce360 PM diagnostic conversation
    When the temporal worker executes the report_defect workflow
    Then the Slack body includes the GitHub issue link
