Feature: S-FB-1 Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a VForce360 System
  I want to ensure that when a defect is reported
  So that the Slack notification contains a valid link to the GitHub issue

  Scenario: Report defect and verify GitHub URL in Slack body
    Given a defect report command is issued for VW-454
    When the report_defect command is executed with valid parameters
    Then the resulting Slack body should contain the GitHub issue URL
    And the notification event metadata should include the project ID
