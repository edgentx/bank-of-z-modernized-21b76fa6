Feature: VW-454 GitHub URL in Slack Body

  Background:
    Given a defect exists with GitHub issue URL "https://github.com/bank-of-z/issues/454"

  Scenario: Verify Slack body contains GitHub URL
    When the defect is reported with title "Verify Link" and URL "https://github.com/bank-of-z/issues/454"
    And the Slack notification is generated
    Then the Slack body should include the GitHub URL
    And the Slack body should contain the text "https://github.com/bank-of-z/issues/454"
    And the validation should pass