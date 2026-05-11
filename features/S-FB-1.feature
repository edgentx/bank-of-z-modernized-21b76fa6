Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Successfully reporting a defect with a valid GitHub URL
    Given a defect report is triggered with valid GitHub URL
    When the report defect command is executed with URL "https://github.com/example/bank-of-z/issues/454"
    Then the resulting Slack body should contain the GitHub issue URL

  Scenario: Failing to report a defect with a missing GitHub URL
    Given a defect report is triggered without a GitHub URL
    When the report defect command is executed with URL null
    Then the system should throw an error indicating the URL is required