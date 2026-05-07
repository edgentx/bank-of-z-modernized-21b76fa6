Feature: Defect Reporting Integration

  Scenario: Validating VW-454 — GitHub URL in Slack body (end-to-end)
    Given a defect reporting system is available
    When the temporal worker triggers _report_defect
    Then the Slack body contains GitHub issue link
