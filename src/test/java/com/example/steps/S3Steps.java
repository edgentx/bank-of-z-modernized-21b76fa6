package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-1");
        // Enroll first to ensure valid state
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV123"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "Jane Doe", "jane@example.com", "GOV456"));
        aggregate.clearEvents();
        // We attempt the update with invalid data in the 'When' step
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "Existing Name", "existing@example.com", "GOV789"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "Active User", "active@example.com", "GOV101"));
        aggregate.clearEvents();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Id is implicitly handled in the 'When' step construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Email is implicitly handled in the 'When' step construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // SortCode is implicitly handled in the 'When' step construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Default successful update params based on "a valid Customer aggregate"
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-1", "Updated Name", "updated@example.com", "GOV999", "1990-01-01", "123456"));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmail() {
        try {
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-1", "Jane Updated", "invalid-email", "GOV456", "1985-05-05", "654321"));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with missing name")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithMissingName() {
        try {
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-1", "", "new@example.com", "GOV789", "1980-01-01", "111111"));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the DeleteCustomerCmd command is executed with active accounts")
    public void theDeleteCustomerCmdCommandIsExecutedWithActiveAccounts() {
        try {
            aggregate.execute(new DeleteCustomerCmd("cust-1", true)); // true = has active accounts
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof CustomerDetailsUpdatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
