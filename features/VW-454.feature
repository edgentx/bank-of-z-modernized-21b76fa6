Feature: VW-454 GitHub URL in Slack Body

  Scenario: Validate GitHub URL is present in Slack notification
    Given the GitHub adapter is configured
    And the Slack notification channel is active
    When _report_defect is triggered via temporal-worker exec
    Then the Slack body contains GitHub issue link
    And the validation no longer exhibits the reported behavior
