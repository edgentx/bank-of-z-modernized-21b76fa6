Feature: Validating VW-454 - GitHub URL in Slack body

  As a VForce360 Support Engineer
  I want defect reports to include the GitHub issue link in the Slack notification
  So that I can quickly navigate to the issue without searching logs.

  Scenario: Triggering defect report includes GitHub URL in Slack notification
    Given a defect report with ID "VW-454" and GitHub URL "https://github.com/example/bank-of-z/issues/454"
    When the defect report is triggered via temporal-worker exec
    Then the Slack body should contain the GitHub issue link
