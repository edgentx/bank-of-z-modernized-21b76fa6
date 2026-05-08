Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify Slack notification includes GitHub link for reported defect
    Given the temporal worker triggers "_report_defect" execution
    When the defect VW-454 is reported with severity LOW
    Then the Slack body contains GitHub issue "<url>"
