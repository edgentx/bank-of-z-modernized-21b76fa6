Feature: Implement DeleteCustomerCmd on Customer (customer-management)

  Scenario: Successfully execute DeleteCustomerCmd
    Given a valid Customer aggregate
    And a valid customerId is provided
    When the DeleteCustomerCmd command is executed
    Then a customer.deleted event is emitted

  Scenario: DeleteCustomerCmd rejected — A customer must have a valid, unique email address and government-issued ID.
    Given a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.
    When the DeleteCustomerCmd command is executed
    Then the command is rejected with a domain error

  Scenario: DeleteCustomerCmd rejected — Customer name and date of birth cannot be empty.
    Given a Customer aggregate that violates: Customer name and date of birth cannot be empty.
    When the DeleteCustomerCmd command is executed
    Then the command is rejected with a domain error

  Scenario: DeleteCustomerCmd rejected — A customer cannot be deleted if they own active bank accounts.
    Given a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.
    When the DeleteCustomerCmd command is executed
    Then the command is rejected with a domain error
