Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 Developer
  I want defect reports to generate a GitHub issue link
  So that the issue can be tracked from Slack notifications

  Scenario: Triggering _report_defect via temporal-worker exec
    Given a defect report command is triggered for project "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
    When the temporal-worker executes the _report_defect workflow
    Then the Slack body should include the GitHub issue link
