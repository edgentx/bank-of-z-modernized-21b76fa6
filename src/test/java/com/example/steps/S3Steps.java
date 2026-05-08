package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private UpdateCustomerDetailsCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Enroll first to satisfy lifecycle
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-123", "John Doe", "john@example.com", "GOV-123"
        ));
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in command construction below
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in command construction below
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in command construction below
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Constructing a valid command for the success path
            cmd = new UpdateCustomerDetailsCmd("cust-123", "Jane Doe", "jane@example.com", "GOV-123", "10-20-30");
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultingEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals("jane@example.com", event.emailAddress());
    }

    // --- Failure Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesEmailAndGovId() {
        customer = new CustomerAggregate("cust-invalid");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-invalid", "Invalid User", "bad-email", "GOV-999"
        ));
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-empty");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-empty", "Empty User", "empty@example.com", "GOV-000"
        ));
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-active");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-active", "Active User", "active@example.com", "GOV-111"
        ));
        // Simulate the state where the customer effectively has active accounts (blocked state)
        customer.markAsDeleted();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid data")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidData() {
        try {
            // We use a trigger. The specific violation depends on the context set in the Given.
            // Since Cucumber contexts are scenario-isolated, we can just pick one that fits the "violates" description generally,
            // or rely on the specific logic in the aggregate.
            // To be precise, we look at the specific violation context.
            
            // Scenario 2: Bad Email/GovId
            if (customer.id().equals("cust-invalid")) {
                cmd = new UpdateCustomerDetailsCmd("cust-invalid", "Invalid User", "bad-email", null, "10-20-30");
            }
            // Scenario 3: Empty Name
            else if (customer.id().equals("cust-empty")) {
                cmd = new UpdateCustomerDetailsCmd("cust-empty", "", "empty@example.com", "GOV-000", "10-20-30");
            }
            // Scenario 4: Active Accounts (simulated deleted state)
            else if (customer.id().equals("cust-active")) {
                cmd = new UpdateCustomerDetailsCmd("cust-active", "Active User", "active@example.com", "GOV-111", "10-20-30");
            } else {
                // Default valid (shouldn't happen in these scenarios)
                cmd = new UpdateCustomerDetailsCmd("x", "Y", "z@z.com", "G", "S");
            }
            
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

}