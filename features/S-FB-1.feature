Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Report defect and verify Slack contains GitHub URL
    Given the defect reporting system is initialized
    And GitHub will return issue URL "https://github.com/example/repo/issues/454"
    When _report_defect is triggered via temporal-worker exec
    Then Slack body contains GitHub issue "https://github.com/example/repo/issues/454"
