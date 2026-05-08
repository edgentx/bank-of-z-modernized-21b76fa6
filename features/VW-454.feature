Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the temporal worker is executing the report_defect workflow

  Scenario: Verify defect report includes GitHub URL in Slack notification
    When a defect report command is triggered with title "Slack link missing" and id "VW-454"
    Then the Slack body should contain the GitHub issue URL
