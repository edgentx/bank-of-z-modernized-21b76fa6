Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Valid defect report includes GitHub URL in Slack notification
    Given a defect report is triggered with id "S-FB-1"
    When the defect reporting workflow executes
    Then the Slack body should include the GitHub issue URL
    And the validation should not fail