Feature: VW-454 GitHub URL in Slack body

  Background:
    Given the Slack notification service is available

  Scenario: Verify GitHub URL is present in Slack notification
    Given a defect has been reported and a GitHub issue created
    When the temporal worker executes the _report_defect workflow
    Then the Slack body should include the GitHub issue URL
