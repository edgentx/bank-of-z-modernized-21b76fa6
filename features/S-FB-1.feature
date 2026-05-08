Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the system requires a defect report for reconciliation failure

  Scenario: Triggering defect report via temporal worker includes GitHub URL
    When the temporal worker executes "report_defect" workflow triggering defect report
    Then the Slack body includes GitHub issue link "https://github.com/bank-of-z/issues/454"
    And the Slack channel is targeted to "#vforce360-issues"
