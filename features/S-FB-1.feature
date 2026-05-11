Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect report successfully includes GitHub URL in Slack notification
    Given a defect report is triggered with ID "VW-454"
    When the validation workflow processes the report
    Then the Slack notification body should include the GitHub issue URL

  Scenario: Defect report fails validation without GitHub URL (Current Bug State)
    Given a defect report is triggered with ID "VW-454"
    When the validation workflow processes the report
    Then the system should log an error if the URL is missing
