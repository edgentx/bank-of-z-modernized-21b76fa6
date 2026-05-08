Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Report defect and verify Slack body contains GitHub URL
    Given a temporal worker execution is triggered for defect reporting
    When the defect VW-454 is reported with severity LOW
    Then the Slack message body should contain the GitHub issue URL
