Feature: Defect Reporting Integration (S-FB-1)

  Scenario: Validating VW-454 — GitHub URL in Slack body (end-to-end)
    Given the defect reporting system is initialized
    And a defect report titled "VW-454 Fix" with description "URL missing in Slack"
    When the defect is reported via Temporal worker
    Then the Slack body should contain the GitHub issue link
    And the validation should succeed