Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 Engineer
  I want defect reports to include a clickable link to the GitHub issue
  So that I can quickly navigate from the Slack alert to the work item

  Background: 
    Given the defect reporting workflow is triggered

  Scenario: Report defect and verify link in Slack body
    When the temporal worker executes "report_defect" workflow
    Then the Slack notification body should contain the GitHub issue URL
