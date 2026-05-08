Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub URL
    Given a defect exists for project "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
    When the defect is reported with severity "LOW"
    Then the Slack body should include the GitHub issue link
