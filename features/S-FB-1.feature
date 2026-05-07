Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a developer or QA engineer
  I want to ensure that when a defect is reported
  The resulting Slack notification contains the link to the created GitHub issue

  Background:
    Given the system is configured with mocked Slack and GitHub adapters

  Scenario: Verify GitHub URL presence in Slack notification after defect report
    Given a reconciliation report exists with ID "batch-12345"
    And the GitHub issue for the defect is created at "https://github.com/example/bank-of-z/issues/454"
    When the defect report is triggered via Temporal worker execution
    Then the Slack notification body should contain the GitHub URL
