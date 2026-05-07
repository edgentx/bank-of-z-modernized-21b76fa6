Feature: Validating VW-454 — GitHub URL in Slack body

  As a VForce360 Engineer
  I want defect reports sent to Slack to include the GitHub issue URL
  So that the issue can be tracked directly from the chat notification

  Scenario: Successfully report defect with valid GitHub URL
    Given a defect report with title "VW-454" and GitHub URL "https://github.com/example/repo/issues/454"
    When the defect is reported via temporal-worker exec
    Then the Slack body contains GitHub issue link

  Scenario: Fail to report defect without GitHub URL
    Given a defect report with title "VW-454" and GitHub URL ""
    When the defect is reported via temporal-worker exec
    Then an error is thrown indicating missing GitHub URL
    And Slack is not notified

  Scenario: Fail to report defect with null GitHub URL
    Given a defect report with title "VW-454" and GitHub URL null
    When the defect is reported via temporal-worker exec
    Then an error is thrown indicating missing GitHub URL
    And Slack is not notified
