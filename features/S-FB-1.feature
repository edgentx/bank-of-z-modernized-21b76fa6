Feature: VW-454 — Validate GitHub URL in Slack Body

  As a VForce360 Engineer
  I want to ensure that when a defect is reported
  The Slack notification contains the correct GitHub issue link

  Scenario: Trigger report_defect and verify Slack body contains GitHub URL
    Given the temporal worker triggers _report_defect with ID "VW-454"
    And the defect title is "GitHub URL missing in Slack"
    When the defect report processing completes
    Then the Slack notification body should contain "https://github.com/"
    And the Slack notification body should contain "issues/"
