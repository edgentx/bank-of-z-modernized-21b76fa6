Feature: Slack Notification GitHub URL Validation (VW-454)

  Background:
    Given the Slack notification service is configured
    And the GitHub integration is enabled
    And the Temporal worker is running

  Scenario: Verify GitHub URL appears in Slack body after defect report
    Given a defect report is triggered with ID "VW-454"
    When the temporal worker executes the report_defect workflow
    Then the Slack message body should contain a GitHub issue URL
    And the GitHub URL should reference defect ID "VW-454"
    And no exception should be thrown during workflow execution
    And the Slack notification should include the GitHub link formatted as "<https://github.com/|GitHub Issue>"

  Scenario: Verify GitHub URL contains proper issue reference
    Given a defect report is triggered with ID "VW-455"
    When the temporal worker executes the report_defect workflow
    Then the Slack message body should contain a GitHub issue URL
    And the GitHub URL should reference defect ID "VW-455"
    And no exception should be thrown during workflow execution