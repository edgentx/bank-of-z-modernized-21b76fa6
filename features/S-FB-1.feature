Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the defect reporting system is active

  Scenario: Verify GitHub URL is present in Slack notification
    When a defect report with GitHub URL "https://github.com/example/bank-of-z-modernization/issues/454"
    And the temporal-worker executes the _report_defect workflow
    Then the Slack body should contain the GitHub issue link
