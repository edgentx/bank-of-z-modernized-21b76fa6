Feature: Implement EndSessionCmd on TellerSession (user-interface-navigation)
  Feature: EndSessionCmd

    Scenario: Successfully execute EndSessionCmd
      Given a valid TellerSession aggregate
      And a valid sessionId is provided
      When the EndSessionCmd command is executed
      Then a session.ended event is emitted

    Scenario: EndSessionCmd rejected — A teller must be authenticated to initiate a session.
      Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
      When the EndSessionCmd command is executed
      Then the command is rejected with a domain error

    Scenario: EndSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
      Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
      When the EndSessionCmd command is executed
      Then the command is rejected with a domain error

    Scenario: EndSessionCmd rejected — Navigation state must accurately reflect the current operational context.
      Given a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.
      When the EndSessionCmd command is executed
      Then the command is rejected with a domain error
