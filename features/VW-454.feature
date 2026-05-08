Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given a defect reporting workflow is initialized

  Scenario: Report defect and verify GitHub URL in Slack
    When the temporal worker triggers _report_defect command
    Then the Slack body includes the GitHub issue URL
    And the validation no longer exhibits the reported behavior
