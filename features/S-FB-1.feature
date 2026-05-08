Feature: VForce360 Defect Reporting (S-FB-1)

  Background:
    Given the VForce360 validation system is active

  Scenario: Verify Slack body contains GitHub URL (VW-454 Regression)
    Given a defect report "VW-454" linked to GitHub issue "https://github.com/bank-of-z/vforce/issues/454"
    When the defect is reported via Temporal worker exec
    Then the Slack body should include the GitHub issue link "https://github.com/bank-of-z/vforce/issues/454"
