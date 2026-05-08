package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customerId = "cust-123";
        aggregate = new CustomerAggregate(customerId);
        // Simulate an enrolled state for a valid customer
        aggregate.setDateOfBirth("1990-01-01");
        // We rely on internal defaults or invariants being met.
        // The command execution checks invariants, so we just need a clean slate.
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        customerId = "cust-invalid-cred";
        aggregate = new CustomerAggregate(customerId);
        // Intentionally leaving state null/blank to trigger validation errors in execute
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customerId = "cust-invalid-info";
        aggregate = new CustomerAggregate(customerId);
        // Intentionally leaving name and dob blank
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customerId = "cust-active-accounts";
        aggregate = new CustomerAggregate(customerId);
        aggregate.setDateOfBirth("1990-01-01"); // Satisfy other invariants
        aggregate.setActiveAccountCount(1); // Trigger violation
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId is already set in the Given steps
        assertNotNull(customerId);
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            aggregate.execute(new DeleteCustomerCmd(customerId));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertTrue(aggregate.isDeleted());
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty());
        assertEquals("customer.deleted", events.get(0).type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
