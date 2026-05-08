Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  As a developer or QA
  I want to ensure that when a defect is reported
  The resulting Slack notification contains the correct link to the GitHub issue.

  Scenario: Verify Slack body contains GitHub URL after reporting defect
    Given a defect reporting workflow is initialized
    And GitHub will return issue URL "https://github.com/example/repo/issues/1"
    When the defect report command is triggered with title "Login Failure"
    Then the Slack body should contain the GitHub issue link
    And the Slack body should not be blank
