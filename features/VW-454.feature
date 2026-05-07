Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the defect reporting system is initialized

  Scenario: Verify Slack notification contains GitHub issue link (Regression Test)
    When the defect report is triggered via temporal-worker exec
    Then the Slack body includes GitHub issue
