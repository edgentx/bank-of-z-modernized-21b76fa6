Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect and verify GitHub URL generation
    Given a defect report is triggered via temporal-worker exec
    When the system processes the defect report command
    Then the Slack body contains the GitHub issue link
    And the validation passes successfully
