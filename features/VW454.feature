Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect and verify Slack body includes GitHub URL
    Given the GitHub issue is created successfully
    When the defect reporting workflow is executed
    Then the Slack notification body includes the GitHub issue URL
