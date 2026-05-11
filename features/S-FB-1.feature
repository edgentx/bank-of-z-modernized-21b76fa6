Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Defect report includes GitHub link in Slack notification
    Given a defect report command is issued with ID VW-454
    When the temporal worker executes the _report_defect workflow
    Then the Slack message body contains the GitHub issue URL
