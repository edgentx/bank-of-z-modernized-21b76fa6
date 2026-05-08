Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Triggering a defect report includes the GitHub URL in the event
    Given a defect report is triggered for VW-454
    When the Slack body is generated from the domain event
    Then the Slack body includes the GitHub issue link
