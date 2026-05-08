Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub issue link
    Given the defect reporting system is initialized
    And a defect report command exists for VW-454
    When the defect report command is executed
    Then the Slack body should include the GitHub issue URL
