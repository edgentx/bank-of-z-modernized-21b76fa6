Feature: Validating VW-454 — GitHub URL in Slack body

  Background: 
    Given the defect reporting system is initialized

  Scenario: Verify Slack notification includes GitHub issue link
    Given a defect report is triggered with ID "VW-454"
    And GitHub issue "https://github.com/example/bank-of-z/issues/454" is created for the defect
    When the validation workflow executes the report_defect activity
    Then the Slack notification body should contain the GitHub URL
