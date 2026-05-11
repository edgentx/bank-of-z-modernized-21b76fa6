Feature: Implement RenderScreenCmd on ScreenMap (user-interface-navigation)

  Scenario: Successfully execute RenderScreenCmd
    Given a valid ScreenMap aggregate
    And a valid screenId is provided
    And a valid deviceType is provided
    When the RenderScreenCmd command is executed
    Then a screen.rendered event is emitted

  Scenario: RenderScreenCmd rejected — All mandatory input fields must be validated before screen submission.
    Given a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.
    When the RenderScreenCmd command is executed
    Then the command is rejected with a domain error

  Scenario: RenderScreenCmd rejected — Field lengths must strictly adhere to legacy BMS constraints during the transition period.
    Given a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.
    When the RenderScreenCmd command is executed
    Then the command is rejected with a domain error
