Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Report defect and verify GitHub URL in Slack notification
    Given a defect report for VW-454 exists
    When the temporal worker triggers the report defect workflow
    Then the Slack body includes the GitHub issue URL
