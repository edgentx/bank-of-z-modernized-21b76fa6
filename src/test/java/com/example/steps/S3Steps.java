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
    private Exception thrownException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // Simulate loading an enrolled customer
        customer = new CustomerAggregate("cust-123");
        // We bypass public setters and simulate state hydration or past events
        // For the purpose of this BDD test, we rely on the aggregate being in a valid state
        // to accept updates. The EnrollCustomerCmd scenario would normally set this up.
        // Here we assume the aggregate is enrolled.
        customer.execute(new EnrollCustomerCmd("cust-123", "John Doe", "john@example.com", "GOV-ID-123"));
        customer.clearEvents(); // Clear enrollment events to focus on updates
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled in the command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicitly handled
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicitly handled
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            customer.execute(new UpdateCustomerDetailsCmd("cust-123", "new.email@example.com", "123456", null, null));
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertFalse(customer.uncommittedEvents().isEmpty());
        assertTrue(customer.uncommittedEvents().get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) customer.uncommittedEvents().get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals("new.email@example.com", event.emailAddress());
        assertEquals("123456", event.sortCode());
    }

    // Failure Scenarios

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesValidEmailAndGovId() {
        customer = new CustomerAggregate("cust-invalid");
        customer.execute(new EnrollCustomerCmd("cust-invalid", "Jane Doe", "jane@example.com", "GOV-ID-999"));
        customer.clearEvents();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmail() {
        try {
            customer.execute(new UpdateCustomerDetailsCmd("cust-invalid", "invalid-email", "123456", null, null));
        } catch (IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-empty");
        customer.execute(new EnrollCustomerCmd("cust-empty", "Existing Name", "existing@example.com", "GOV-ID-111"));
        customer.clearEvents();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyName() {
        try {
            customer.execute(new UpdateCustomerDetailsCmd("cust-empty", "valid@example.com", "123456", "   ", null));
        } catch (IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-accounts");
        customer.execute(new EnrollCustomerCmd("cust-accounts", "Rich User", "rich@example.com", "GOV-ID-888"));
        customer.clearEvents();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with delete intention")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithDeleteIntention() {
        // This scenario maps to testing the DeleteCustomerCmd logic within the aggregate or a specific update flag.
        // Based on AC, we simulate the rejection condition.
        try {
             // The AC phrasing "UpdateCustomerDetailsCmd rejected..." implies the update logic might be checking invariants.
             // However, strictly speaking, deletion is a separate command (DeleteCustomerCmd).
             // Assuming the AC implies testing the invariant via the aggregate's Execute method.
             // Here we test the Delete path for the aggregate.
             customer.execute(new DeleteCustomerCmd("cust-accounts", true)); // true = has active accounts
        } catch (IllegalStateException e) {
            this.thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
