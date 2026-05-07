Feature: Fix Validating VW-454 — GitHub URL in Slack body

  Background:
    Given the system is operating in the test environment

  Scenario: Verify defect report generates GitHub URL for downstream consumers
    Given a defect report command is issued for story "S-FB-1"
    When the defect is reported with severity "LOW" and component "validation"
    Then the system should generate a GitHub issue URL
    And the aggregate state should reflect the GitHub link
    And the event payload contains the GitHub link for Slack
    And the validation passes confirming the fix for VW-454

  Scenario Outline: Verify GitHub URL is present for various severities
    Given a defect report command is issued for story "S-FB-<id>"
    When the defect is reported with severity "<severity>" and component "validation"
    Then the system should generate a GitHub issue URL
    And the aggregate state should reflect the GitHub link

    Examples:
      | id   | severity |
      | 1    | LOW      |
      | 2    | MEDIUM   |
      | 3    | HIGH     |
