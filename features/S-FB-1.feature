Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 PM
  I want reported defects to automatically link to the created GitHub issue
  So that I can quickly navigate from Slack to the issue tracker

  Scenario: Triggering report_defect workflow includes GitHub URL in Slack
    Given a temporal worker is available for defect reporting
    And a valid defect report command with id "VW-454"
    When the _report_defect workflow is triggered
    Then the Slack body should contain the GitHub issue link
