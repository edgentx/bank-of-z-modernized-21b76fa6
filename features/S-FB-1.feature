Feature: Validating VW-454 — GitHub URL in Slack body

  Scenario: Defect report includes valid GitHub link
    Given a defect report with ID "VW-454" is triggered
    When the temporal worker executes the report defect workflow
    Then the Slack body includes the GitHub issue URL
    And the Slack body does not contain placeholder text
