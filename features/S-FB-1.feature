Feature: S-FB-1 Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the Slack notification service is available

  Scenario: Verify Slack body includes GitHub issue URL
    When a defect report is triggered with issue VW-454 and URL "https://github.com/egdcrypto/bank-of-z-modernized/issues/454"
    Then the Slack body includes the GitHub issue link

  Scenario: Validate behavior when URL is missing
    Given the defect report is generated without a URL
    When the report is sent to Slack
    Then the Slack body should be validated successfully but missing the link