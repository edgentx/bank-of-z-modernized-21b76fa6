Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Successfully report a defect with a valid GitHub URL
    Given a defect report is triggered with title "VW-454", severity "LOW", and GitHub URL "https://github.com/example/bank-of-z/issues/1"
    When the temporal worker executes the report_defect workflow
    Then the Slack body contains the GitHub issue link

  Scenario: Fail to report a defect with an invalid GitHub URL
    Given a defect report is triggered with title "VW-454", severity "LOW", and GitHub URL "not-a-url"
    When the temporal worker executes the workflow with an invalid URL
    Then the validation prevents execution
