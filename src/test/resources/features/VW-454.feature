Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Trigger report_defect and verify GitHub link
    Given the GitHub adapter is available
    When _report_defect is triggered via temporal-worker exec
    Then Slack body includes GitHub issue: "https://github.com/example/bank-of-z/issues/"
    And the validation no longer exhibits the reported behavior
