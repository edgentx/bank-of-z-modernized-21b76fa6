Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack body contains GitHub issue link
    Given a defect is reported with ID "VW-454"
    When the defect reporting workflow is executed
    Then the resulting event payload contains a valid GitHub URL
    And the URL includes the defect ID "VW-454"
