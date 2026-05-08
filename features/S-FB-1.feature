Feature: Validate GitHub URL in Slack Body (VW-454)

  Background:
    Given a defect report command is issued

  Scenario: Successful defect report with valid GitHub URL in Slack body
    When the defect is reported and Slack notification is triggered
    Then the Slack body should include the GitHub issue link

  Scenario: Slack notification fails if GitHub URL is missing
    When the Slack body is missing the GitHub URL
    Then the validation should fail with an error
