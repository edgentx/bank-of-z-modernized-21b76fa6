Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Triggering report_defect includes GitHub URL in Slack body
    Given the defect reporting system is initialized
    When _report_defect is triggered via temporal-worker exec with GitHub issue "https://github.com/bank-of-z/vforce360/issues/454"
    Then the Slack body includes GitHub issue: "https://github.com/bank-of-z/vforce360/issues/454"

  Scenario: Triggering report_defect with invalid URL fails validation
    Given the defect reporting system is initialized
    When _report_defect is triggered via temporal-worker exec with GitHub issue ""
    Then the validation fails and Slack is not notified
