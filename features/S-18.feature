Feature: Implement StartSessionCmd on TellerSession (user-interface-navigation)

  Scenario: Successfully execute StartSessionCmd
    Given a valid TellerSession aggregate
    And a valid tellerId is provided
    And a valid terminalId is provided
    When the StartSessionCmd command is executed
    Then a session.started event is emitted

  Scenario: StartSessionCmd rejected — A teller must be authenticated to initiate a session.
    Given a valid TellerSession aggregate
    And a null tellerId is provided
    And a valid terminalId is provided
    When the StartSessionCmd command is executed
    Then the command is rejected with a domain error

  Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
    Given a valid TellerSession aggregate
    And a valid tellerId is provided
    And a valid terminalId is provided
    And the session state is active
    And the last activity was 20 minutes ago
    When the StartSessionCmd command is executed
    Then the command is rejected with a domain error

  Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context.
    Given a valid TellerSession aggregate
    And a valid tellerId is provided
    And a null terminalId is provided
    When the StartSessionCmd command is executed
    Then the command is rejected with a domain error