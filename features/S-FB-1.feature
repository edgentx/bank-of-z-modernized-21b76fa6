Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Successful defect reporting includes GitHub URL in Slack notification
    Given a defect report is triggered with title "Validation Error" and description "URL is missing"
    When the defect is processed by the domain layer
    Then the resulting Slack body should contain the GitHub issue URL
    And the validation should pass
