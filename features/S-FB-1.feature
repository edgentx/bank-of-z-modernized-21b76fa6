Feature: S-FB-1 - Validating VW-454 (GitHub URL in Slack body)

  Scenario: Defect report workflow should include GitHub URL in Slack notification
    Given a defect report command is triggered
    When the system processes the report_defect workflow
    Then the Slack notification body contains the GitHub issue URL
