Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify GitHub URL is present in Slack notification body
    Given the defect reporting workflow is triggered
    When the temporal-worker reports a defect with ID "S-FB-1" and GitHub URL "https://github.com/example/bank-of-z-modernization/issues/454"
    Then the Slack body should contain the GitHub issue link "https://github.com/example/bank-of-z-modernization/issues/454"
