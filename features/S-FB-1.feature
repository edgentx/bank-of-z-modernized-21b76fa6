Feature: VForce360 Defect Reporting Integration

  Scenario: Verify GitHub URL is included in Slack notification body (VW-454)
    Given a defect "VW-454" exists in the system
    And the Slack notification service is available
    When the temporal worker executes the report defect workflow for issue "VW-454"
    Then the Slack body contains the GitHub issue link
