Feature: Defect Validation VW-454
  As a VForce360 Support Engineer
  I want defect reports to include a link to the GitHub issue
  So that I can quickly jump from Slack to the code repository

  Scenario: Trigger defect report via temporal worker
    Given the temporal worker is initialized with mock adapters
    And a defect report command is triggered
    When the worker executes the _report_defect workflow
    Then the Slack message body contains the GitHub issue URL
    And the GitHub issue link is clearly formatted
