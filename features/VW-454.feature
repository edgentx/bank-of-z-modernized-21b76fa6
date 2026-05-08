Feature: Validating VW-454 - GitHub URL in Slack body

  Scenario: Trigger report_defect and verify GitHub URL in Slack body
    Given a defect report is generated for VW-454
    When the defect report workflow is executed
    Then the Slack notification body contains the GitHub issue URL
