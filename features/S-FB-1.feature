Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack notification includes GitHub link after workflow execution
    Given the temporal worker executes the defect reporting workflow
    And a GitHub issue is created for defect VW-454
    When the workflow completes reporting the defect
    Then the Slack message body contains the GitHub issue URL
