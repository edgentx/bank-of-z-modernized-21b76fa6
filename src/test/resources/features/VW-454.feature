Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify GitHub URL presence in Slack notification body
    Given a defect report triggers the temporal workflow
    When the GitHub issue is created successfully
    Then the Slack body contains the GitHub issue link