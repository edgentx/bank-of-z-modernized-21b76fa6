Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given a defect report command for issue "VW-454" with URL "https://github.com/bank-of-z/issues/issues/454"

  Scenario: Valid GitHub URL is present in Slack body
    When the defect is reported with title "VW-454" and GitHub URL "https://github.com/bank-of-z/issues/issues/454"
    Then the Slack body should contain "GitHub Issue: <https://github.com/bank-of-z/issues/issues/454>"

  Scenario: Invalid GitHub URL is rejected
    When the defect is reported with title "VW-454" and GitHub URL "https://jira.example.com/browse/VW-454"
    Then the validation should fail with error containing "valid GitHub Issue URL"
