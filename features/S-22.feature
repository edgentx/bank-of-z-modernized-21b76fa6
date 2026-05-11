Feature: Implement ValidateScreenInputCmd on ScreenMap (user-interface-navigation)

  Scenario: Successfully execute ValidateScreenInputCmd
    Given a valid ScreenMap aggregate
    And a valid screenId is provided
    And a valid inputFields is provided
    When the ValidateScreenInputCmd command is executed
    Then a input.validated event is emitted

  Scenario: ValidateScreenInputCmd rejected — All mandatory input fields must be validated before screen submission.
    Given a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.
    When the ValidateScreenInputCmd command is executed
    Then the command is rejected with a domain error

  Scenario: ValidateScreenInputCmd rejected — Field lengths must strictly adhere to legacy BMS constraints during the transition period.
    Given a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.
    When the ValidateScreenInputCmd command is executed
    Then the command is rejected with a domain error
