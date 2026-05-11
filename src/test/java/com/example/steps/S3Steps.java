package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private DomainEvent resultingEvent;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-1");
        aggregate.setEnrolled(true);
        aggregate.setFullName("John Doe");
        aggregate.setEmail("john@example.com");
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled by the aggregate initialization
    }

    @Given("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicitly handled by command creation in When
    }

    @Given("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicitly handled by command creation in When
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailOrGovId() {
        aggregate = new CustomerAggregate("cust-bad");
        aggregate.setEnrolled(true);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameOrDob() {
        aggregate = new CustomerAggregate("cust-empty");
        aggregate.setEnrolled(true);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-active");
        aggregate.setEnrolled(true);
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // We construct a command that might be valid or invalid depending on the scenario.
            // For the scenarios where specific violations are expected, we pass data that triggers them.
            var cmd = new UpdateCustomerDetailsCmd(
                aggregate.id(),
                "Jane Doe", // Valid name for success, overridden if needed for error cases
                "jane@example.com", // Valid email
                "123456", // Valid sortCode
                "GOV123" // Valid GovId
            );
            
            // Handle specific invalid data injection for violation scenarios based on context
            if (aggregate.id().equals("cust-bad")) {
                 cmd = new UpdateCustomerDetailsCmd(aggregate.id(), "Jane", "invalid-email", "123456", null);
            } else if (aggregate.id().equals("cust-empty")) {
                 cmd = new UpdateCustomerDetailsCmd(aggregate.id(), "", "jane@example.com", "123456", "GOV123");
            }

            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            // If the ID implies active accounts, we set the flag true
            boolean hasActive = aggregate.id().equals("cust-active");
            var cmd = new DeleteCustomerCmd(aggregate.id(), hasActive);
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultingEvent);
        assertTrue(resultingEvent instanceof CustomerDetailsUpdatedEvent);
        assertEquals("customer.details.updated", resultingEvent.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNotNull(resultingEvent);
        assertTrue(resultingEvent instanceof CustomerDeletedEvent);
        assertEquals("customer.deleted", resultingEvent.type());
    }
}
