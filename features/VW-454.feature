Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the system is ready to report defects

  Scenario: Slack body includes GitHub issue link after defect reporting
    Given GitHub will return issue URL "https://github.com/mock/repo/issues/454"
    When _report_defect is triggered via temporal-worker exec
    Then the Slack body should include the GitHub issue link
