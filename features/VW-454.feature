Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the defect reporting system is initialized

  Scenario: Verify GitHub URL is present in Slack notification
    When _report_defect is triggered via temporal-worker exec for VW-454
    Then the Slack body contains the GitHub issue link
    And the link is formatted correctly
