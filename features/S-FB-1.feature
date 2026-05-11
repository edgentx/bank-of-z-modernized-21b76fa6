Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given the application context is loaded

  Scenario: Verify Slack notification contains valid GitHub link
    Given a defect report is triggered from VForce360
    When the defect reporting workflow executes
    Then the Slack body contains the GitHub issue URL
    And the validation no longer exhibits the reported behavior
