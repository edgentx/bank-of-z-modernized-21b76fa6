Feature: NavigateMenuCmd

  Scenario: Successfully execute NavigateMenuCmd
    Given a valid TellerSession aggregate
    And a valid sessionId is provided
    And a valid menuId is provided
    And a valid action is provided
    When the NavigateMenuCmd command is executed
    Then a menu.navigated event is emitted

  Scenario: NavigateMenuCmd rejected — A teller must be authenticated to initiate a session.
    Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
    When the NavigateMenuCmd command is executed
    Then the command is rejected with a domain error

  Scenario: NavigateMenuCmd rejected — Sessions must timeout after a configured period of inactivity.
    Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
    When the NavigateMenuCmd command is executed
    Then the command is rejected with a domain error

  Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect the current operational context.
    Given a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.
    When the NavigateMenuCmd command is executed
    Then the command is rejected with a domain error
