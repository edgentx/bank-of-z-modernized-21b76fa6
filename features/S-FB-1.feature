Feature: S-FB-1 Fix: Validating VW-454 — GitHub URL in Slack body (end-to-end)

  Background:
    Given a defect reporting workflow is initialized via temporal-worker exec

  Scenario: Verify Slack body contains valid GitHub issue link
    When the defect report containing GitHub link is generated
    Then the Slack body should include the GitHub issue URL

  Scenario: Verify build stability post-fix
    # The previous attempt failed because of a malformed POM.
    # We run tests to ensure the project structure remains valid.
    Given the defect reporting workflow is initialized
    Then the build system shall recognize the correct Maven POM structure
