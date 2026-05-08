Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect includes GitHub link in Slack notification
    Given a defect report is ready to be sent
    When the temporal worker triggers "report_defect" via exec
    Then the Slack body contains the GitHub issue link
    And the GitHub issue link is formatted correctly
