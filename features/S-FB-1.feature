Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect reporting workflow is initialized

  Scenario: Report defect and verify GitHub link is present
    Given a defect report for ticket "VW-454" exists
    When the temporal-worker executes the report_defect command
    Then the Slack body contains the GitHub issue link
    And the Slack body includes GitHub issue "https://github.com/21b76fa6-afb6-4593-9e1b-b5d7548ac4d1/issues/VW-454"
