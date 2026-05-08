Feature: VForce360 Slack Integration Validation

  Scenario: Validating VW-454 — GitHub URL in Slack body
    Given a GitHub issue URL "https://github.com/example/bank-of-z/issues/454"
    And a defect title "VW-454: Validation Error"
    When the defect is reported via Temporal workflow
    Then the Slack body should contain the GitHub issue link
