Feature: Defect Reporting Validation (S-FB-1)

  Background:
    Given the system is initialized with mock adapters

  Scenario: Validating VW-454 — GitHub URL in Slack body (end-to-end)
    When a defect report "VW-454" is triggered via temporal-worker exec
    Then the Slack notification body should contain "https://github.com/example/bank-of-z/issues/"
    And the validation should pass without error
