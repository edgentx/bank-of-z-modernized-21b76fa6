Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Triggering defect report includes GitHub URL in Slack notification
    Given the Temporal worker triggers _report_defect
    And the GitHub issue URL is "https://github.com/bank-of-z/vforce360/issues/454"
    When the defect report is processed
    Then the Slack body includes the GitHub issue URL "https://github.com/bank-of-z/vforce360/issues/454"
    And the message is posted to channel "vforce360-issues"
