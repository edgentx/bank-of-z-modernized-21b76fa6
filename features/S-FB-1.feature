Feature: S-FB-1 Validating VW-454 — GitHub URL in Slack body

  Background:
    Given a defect report trigger is received

  Scenario: Validate GitHub URL is present in Slack notification
    When the temporal-worker executes _report_defect
    Then the Slack body contains the GitHub issue link
