Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Valid Defect Report Creation
    Given a defect report command exists
    When the defect report is executed
    Then the defect should be saved with reported status

  Scenario: Linking Valid GitHub Issue
    Given a defect has been reported
    When a GitHub issue link command is executed with URL "https://github.com/org/repo/issues/1"
    Then the aggregate should contain the GitHub URL
    And a GitHubIssueLinkedEvent should be emitted

  Scenario: Rejecting Invalid GitHub URL
    Given a defect exists
    When the system tries to link an invalid GitHub URL "https://gitlab.com/org/repo/issues/1"
    Then the command should fail with an error
    And no GitHub issue event should be emitted