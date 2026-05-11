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
    private Throwable thrownException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Hydrate manually as we don't have a full repository in unit steps
        customer.execute(new EnrollCustomerCmd("cust-123", "John Doe", "john.doe@example.com", "GOV-123"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        customer = new CustomerAggregate("cust-invalid");
        // Existing state is irrelevant for this specific command validation, 
        // but we assume the aggregate exists.
        customer.execute(new EnrollCustomerCmd("cust-invalid", "Jane Doe", "jane@example.com", "GOV-999"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-empty");
        customer.execute(new EnrollCustomerCmd("cust-empty", "Existing Name", "existing@example.com", "GOV-888"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-active");
        customer.execute(new EnrollCustomerCmd("cust-active", "Active User", "active@example.com", "GOV-777"));
        customer.clearEvents();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Context is usually handled by the aggregate initialization
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in the When step via Command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in the When step via Command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Determine valid or invalid data based on the context/Given
            // Ideally, we'd extract this from the scenario context, but hardcoding for S-3 paths:
            
            String email = "new.email@example.com";
            String sortCode = "123456";
            String name = customer.getFullName();
            String dob = "1990-01-01";

            if (customer.id().equals("cust-invalid")) {
                email = "invalid-email"; // Triggers validation
            } else if (customer.id().equals("cust-empty")) {
                name = null; // Triggers validation
            }

            // Execute command
            customer.execute(new UpdateCustomerDetailsCmd(customer.id(), name, email, dob, sortCode));
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertFalse(customer.uncommittedEvents().isEmpty());
        assertTrue(customer.uncommittedEvents().get(0) instanceof CustomerDetailsUpdatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
