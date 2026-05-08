Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Verify defect report includes GitHub link in Slack notification
    Given a defect report is generated with GitHub issue URL "https://github.com/org/repo/issues/454"
    When the defect report is processed and sent to Slack
    Then the Slack body should contain the text "https://github.com/org/repo/issues/454"