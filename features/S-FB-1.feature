Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub link
    Given a defect report command exists
    When the temporal worker executes the defect report workflow
    Then the Slack body should contain the GitHub issue link
