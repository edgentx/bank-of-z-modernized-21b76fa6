Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification includes GitHub link when defect is reported
    Given the system is ready to report defects
    When the temporal worker triggers defect reporting for issue VW-454
    Then the Slack notification body must contain the GitHub issue URL
