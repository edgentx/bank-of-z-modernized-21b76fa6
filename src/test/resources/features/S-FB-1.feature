Feature: S-FB-1 Validating VW-454 — GitHub URL in Slack body

  As a VForce360 Support Engineer
  I want defect reports to include the GitHub issue URL in the Slack notification
  So that I can quickly navigate to the issue from the chat log

  Scenario: Report defect via Temporal worker
    Given a defect report is generated for issue VW-454
    When the report_defect workflow executes
    Then the Slack body includes the GitHub issue URL
