Feature: VW-454 GitHub URL Validation

  Scenario: Verify defect report includes GitHub link in Slack body
    Given a defect report is triggered
    When the temporal worker executes "VW-454" with description "URL missing in Slack body"
    Then the Slack body should contain the GitHub issue URL
    And the Slack body should not be empty
