Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given a defect report trigger is received via temporal-worker exec

  Scenario: Verify Slack body includes GitHub issue link after defect report
    When the system executes the report_defect workflow
    Then the Slack body contains the GitHub issue link
