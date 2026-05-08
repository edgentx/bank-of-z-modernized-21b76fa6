Feature: Implement UpdateCustomerDetailsCmd on Customer (customer-management)
  Feature: UpdateCustomerDetailsCmd

    Scenario: Successfully execute UpdateCustomerDetailsCmd
      Given a valid Customer aggregate
      And a valid customerId is provided
      And a valid emailAddress is provided
      And a valid sortCode is provided
      When the UpdateCustomerDetailsCmd command is executed
      Then a customer.details.updated event is emitted

    Scenario: UpdateCustomerDetailsCmd rejected — A customer must have a valid, unique email address and government-issued ID.
      Given a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.
      When the UpdateCustomerDetailsCmd command is executed
      Then the command is rejected with a domain error

    Scenario: UpdateCustomerDetailsCmd rejected — Customer name and date of birth cannot be empty.
      Given a Customer aggregate that violates: Customer name and date of birth cannot be empty.
      When the UpdateCustomerDetailsCmd command is executed
      Then the command is rejected with a domain error

    Scenario: UpdateCustomerDetailsCmd rejected — A customer cannot be deleted if they own active bank accounts.
      Given a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.
      When the UpdateCustomerDetailsCmd command is executed
      Then the command is rejected with a domain error