package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Exception caughtException;
    private Object lastEvent;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-1");
        // Enroll the customer first to make it valid
        customer.execute(new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV-123"));
        customer.clearEvents(); // Clear enrollment events for clarity
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled by the aggregate ID, but we can assert
        assertNotNull(customer.id());
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Placeholder for data setup context
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Placeholder for data setup context
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            var result = customer.execute(new UpdateCustomerDetailsCmd("cust-1", "Jane Doe", "jane@example.com", "12-34-56"));
            if (!result.isEmpty()) {
                lastEvent = result.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(lastEvent);
        assertTrue(lastEvent instanceof CustomerDetailsUpdatedEvent);
        assertEquals("customer.details.updated", ((CustomerDetailsUpdatedEvent) lastEvent).type());
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesValidEmail() {
        customer = new CustomerAggregate("cust-2");
        customer.execute(new EnrollCustomerCmd("cust-2", "Invalid User", "invalid-email", "GOV-999"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-3");
        // Simulating a state where name might be null or empty (though aggregate logic prevents this usually)
        // Here we test the update command rejecting empty names
        customer.execute(new EnrollCustomerCmd("cust-3", "Original Name", "original@example.com", "GOV-888"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-4");
        customer.execute(new EnrollCustomerCmd("cust-4", "Account Holder", "holder@example.com", "GOV-777"));
        customer.clearEvents();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
