Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Valid Defect Report includes GitHub URL in Slack Body
    Given a defect report is triggered for VW-454
    When the temporal-worker executes _report_defect logic
    Then the Slack body contains the GitHub issue link
