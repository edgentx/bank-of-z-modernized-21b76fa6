package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Command command;
    private Throwable thrownException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Enroll the customer first to establish a valid base state
        customer.execute(new EnrollCustomerCmd("cust-123", "John Doe", "old@example.com", "GOV-123"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        // Scenario: No Government ID (not enrolled/ID null) OR Email not unique/invalid
        customer = new CustomerAggregate("cust-invalid");
        // Setup state where ID is missing or email is invalid/unchanged
        customer.execute(new EnrollCustomerCmd("cust-invalid", "Jane Doe", "jane@example.com", ""));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-empty-name");
        // Manually corrupt state to simulate the violation scenario for 'Update'
        // or rely on the Aggregate's initialization checks.
        // Here we assume an existing customer where name became null (data integrity issue)
        // Since constructor is minimal, we assume the violation occurs during the update attempt
        // checks against internal state.
        customer.execute(new EnrollCustomerCmd("cust-empty-name", "", "test@example.com", "GOV-999"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-active-acc");
        customer.execute(new EnrollCustomerCmd("cust-active-acc", "Active User", "active@example.com", "GOV-888"));
        // Use helper to set the flag checked by the aggregate logic
        customer.setHasActiveAccounts(true);
        customer.clearEvents();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in command construction steps
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in command construction steps
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in command construction steps
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // We construct a command that is intended to be valid, unless the scenario setup forces a failure
        // via the Aggregate's internal state (hasActiveAccounts, missing ID, etc.)
        // or invalid data in the command itself.
        try {
            command = new UpdateCustomerDetailsCmd(customer.id(), "new-different-email@example.com", "123456");
            customer.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertFalse(customer.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertTrue(customer.uncommittedEvents().get(0) instanceof CustomerDetailsUpdatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Should have thrown an exception");
        // Verify it's an exception type expected (IllegalArgumentException or IllegalStateException)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

}
