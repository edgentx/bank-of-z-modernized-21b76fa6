Feature: Validate VForce360 Defect Reporting (VW-454)

  Scenario: Valid defect report contains GitHub URL
    Given a defect report is generated with GitHub URL "https://github.com/example/bank-of-z/issues/454"
    When the validation logic runs
    Then the Slack body should contain a valid GitHub issue link

  Scenario: Invalid defect report misses GitHub URL
    Given a defect report is generated without a GitHub URL
    When the validation logic runs
    Then the Slack body should be marked as invalid
