Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 Support Engineer
  I want defect reports sent to Slack to include the GitHub Issue URL
  So that I can quickly navigate to the tracking ticket without searching the repository

  Scenario: Verify defect report includes GitHub URL
    Given the Slack notification system is initialized
    When the temporal worker triggers the _report_defect workflow with GitHub issue "https://github.com/bank-of-z/core/issues/454"
    Then the Slack message body should contain the GitHub issue link
    And the Slack message body should contain the text "https://github.com/bank-of-z/core/issues/454"
