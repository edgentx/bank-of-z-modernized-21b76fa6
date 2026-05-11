Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Reporting a defect should generate a Slack message with the GitHub URL
    Given a defect report command is triggered
    When the system processes the defect report
    Then the resulting event contains a valid GitHub issue URL
    And the Slack notification body includes the GitHub issue link