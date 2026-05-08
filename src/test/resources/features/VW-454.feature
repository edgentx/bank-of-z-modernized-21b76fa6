Feature: GitHub URL in Slack Body (VW-454)

  As a VForce360 Engineer
  I want defect reports in Slack to include a direct link to the GitHub issue
  So that I can quickly navigate to the ticket from the alert

  Scenario: Defect report includes GitHub URL
    Given a reconciliation has failed and a defect is detected
    When the temporal workflow executes the report_defect command
    Then the Slack message body must contain the GitHub issue URL

  Scenario: Defect report validation prevents invalid IDs
    When the temporal workflow executes report_defect with an invalid ID
    Then the workflow should reject the command and Slack should not be notified
