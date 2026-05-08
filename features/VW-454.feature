Feature: Validating VW-454 GitHub URL in Slack body

  Background:
    Given the Slack notification service is available

  Scenario: Report defect via temporal-worker includes GitHub link
    Given a defect report command for VW-454 is prepared
    When the report_defect workflow is executed
    Then the Slack notification body includes the GitHub issue URL
