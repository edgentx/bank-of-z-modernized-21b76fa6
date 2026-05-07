Feature: Validate GitHub URL in Slack Body

  Background:
    Given the defect reporting system is initialized

  Scenario: Regression test for VW-454 - Slack body must include GitHub URL
    Given a defect report command is issued with id "VW-454"
    When the system processes the defect report via Temporal worker
    Then the resulting Slack body should contain the GitHub issue URL