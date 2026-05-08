Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the defect report "VW-454" has been triggered via temporal-worker

  Scenario: Verify Slack body contains GitHub issue link (Happy Path)
    And the Slack message body contains the GitHub issue URL
    When I verify the Slack body for defect "VW-454"
    Then the body should include the GitHub issue URL

  Scenario: Regression test for missing GitHub URL (Bug detection)
    And the Slack message body is missing the GitHub URL
    When I verify the Slack body for defect "VW-454"
    Then the validation should fail indicating the URL is missing