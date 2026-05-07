Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 Support Engineer
  I want defect reports to include the GitHub issue link in the Slack notification
  So that I can quickly navigate to the issue without searching

  Scenario: Trigger report_defect and verify GitHub link presence
    Given the defect VW-454 has been reported
    When _report_defect is triggered via temporal-worker exec
    Then the Slack body contains the GitHub issue link

  Scenario: Regression check for missing URL
    Given the defect VW-454 has been reported
    When _report_defect is triggered via temporal-worker exec
    Then the Slack body includes text "Defect Reported: VW-454"
