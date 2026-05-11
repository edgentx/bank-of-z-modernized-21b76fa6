Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub URL when reporting a defect
    Given the defect reporting system is initialized
    When I trigger report_defect with title "VW-454 Defect"
    Then the Slack body should contain the GitHub issue link