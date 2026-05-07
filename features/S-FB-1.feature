Feature: Validating VW-454 — GitHub URL in Slack body

  As a developer or QA engineer
  I want to ensure that when a defect is reported via the Temporal worker
  The resulting Slack notification contains a valid link to the created GitHub issue

  Background:
    Given the system is ready to report a defect

  Scenario: Report defect and verify Slack body contains GitHub URL
    When the report_defect workflow is triggered via temporal-worker exec
    Then the Slack body contains the GitHub issue link
