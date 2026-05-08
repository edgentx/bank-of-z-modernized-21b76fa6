Feature: S-FB-1 Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification contains GitHub issue URL
    When the defect report VW-454 is triggered via temporal-worker exec
    Then the Slack message body must contain the GitHub issue URL