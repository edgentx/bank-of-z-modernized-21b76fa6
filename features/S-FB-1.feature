Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect report includes GitHub URL in Slack body
    Given a defect report is initiated with ID "VW-454" and title "Fix: Validating VW-454"
    When the defect is reported with severity "LOW" and component "validation"
    Then the resulting event should contain a valid GitHub URL
    And the Slack body should contain the GitHub URL link
