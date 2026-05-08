Feature: VW-454 Regression Test

  Scenario: Validating GitHub URL presence in Slack notification body
    Given a defect report command exists for VW-454
    When the report_defect workflow is executed via temporal-worker
    Then the Slack body must include the GitHub issue URL
