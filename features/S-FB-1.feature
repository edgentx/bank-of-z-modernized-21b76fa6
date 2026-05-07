Feature: VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify defect reporting includes GitHub URL in Slack message
    Given a defect report with GitHub URL "https://github.com/bank-of-z/issues/454"
    And the target Slack channel is "#vforce360-issues"
    When the defect report is processed
    Then the Slack body contains the GitHub URL