Feature: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Scenario: Successful defect reporting with GitHub URL in Slack body
    Given a defect report is triggered with ID "VW-454"
    And the defect title is "GitHub URL Missing"
    When the system processes the report command
    Then the resulting event should contain a GitHub URL
    And the resulting event should contain a Slack body
    And the Slack body should include the GitHub URL
    And the Slack body should include the defect title

  Scenario: Regression verification of Slack payload content
    Given a defect report is triggered with ID "VW-454-REG"
    And the defect title is "Regression Test"
    When the system processes the report command
    And I verify the Slack payload sent to the mock adapter
    Then the payload must explicitly contain the link line

  Scenario: Invalid defect report missing title
    Given a defect report is triggered with ID "VW-456"
    And the defect title is ""
    When the system processes the report command
    Then the validation should fail if the title is missing
