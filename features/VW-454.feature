Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Verify Slack body contains GitHub URL after defect reporting
    Given a defect report is ready for VForce360
    And the GitHub service is available
    When the defect is reported via the temporal-worker exec
    Then the Slack body contains the GitHub issue link
