Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect reporting includes GitHub URL in Slack notification
    Given a defect report with GitHub issue URL "https://github.com/example-org/repo/issues/454"
    When the defect is reported via Temporal worker execution
    Then the Slack notification body should include the GitHub issue link
    And the validation should pass successfully
