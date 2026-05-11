Feature: Validating VW-454 — GitHub URL in Slack body

  Background:
    Given a defect report is triggered via temporal-worker exec

  Scenario: Report Defect generates correct Slack body
    When the report_defect workflow completes
    Then the Slack body contains the GitHub issue link
