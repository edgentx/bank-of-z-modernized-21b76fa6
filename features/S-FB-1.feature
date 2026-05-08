Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given a validation aggregate exists

  Scenario: Report defect generates Slack body with GitHub URL
    Given the defect report for VW-454 is valid
    When I trigger the defect report command
    Then the system creates a GitHub issue
    And the Slack notification body includes the GitHub URL
    And the event contains valid links
