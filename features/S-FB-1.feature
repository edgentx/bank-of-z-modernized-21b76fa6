Feature: VW-454 — GitHub URL in Slack body (End-to-End)

  As a developer
  I want reported defects to create a GitHub issue
  So that the link to that issue is visible in Slack

  Scenario: Triggering defect report includes GitHub link in Slack body
    Given the VForce360 temporal worker is ready
    When the defect "VW-454" is reported with description "Validation Failure"
    Then a GitHub issue should be created
    And the Slack notification body should contain the GitHub issue URL
