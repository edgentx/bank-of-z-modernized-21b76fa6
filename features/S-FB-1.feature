Feature: Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack body contains GitHub issue link when defect is reported
    Given a defect report command for VW-454
    When the defect report is executed via temporal-worker
    Then the Slack body contains the GitHub issue link
    And the validation no longer exhibits the reported behavior
