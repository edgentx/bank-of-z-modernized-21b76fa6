Feature: Defect Reporting and Validation (VW-454)

  Scenario: Validating VW-454 - GitHub URL in Slack body (end-to-end)
    Given a defect exists with title "VW-454"
    When the defect is reported via temporal-worker exec
    Then the Slack body contains GitHub issue link