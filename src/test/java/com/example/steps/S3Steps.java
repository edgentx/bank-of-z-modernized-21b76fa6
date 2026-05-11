package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private String customerId = "cust-123";

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate(customerId);
        // Enroll first to create a valid state
        aggregate.execute(new EnrollCustomerCmd(customerId, "John Doe", "john@example.com", "GOV-ID-123"));
        aggregate.clearEvents(); // Clear enrollment events to focus on test
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Setup in constructor/Given
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in the When step construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in the When step construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            aggregate.execute(
                new UpdateCustomerDetailsCmd(
                    customerId,
                    "Jane Doe",
                    "jane.doe@example.com",
                    "10-20-30",
                    "1990-01-01"
                )
            );
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty());
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) aggregate.uncommittedEvents().get(0);
        Assertions.assertEquals("customer.details.updated", event.type());
        Assertions.assertEquals("Jane Doe", event.fullName());
        Assertions.assertNull(capturedException);
    }

    // Negative Scenarios

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesValidEmailAndId() {
        aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new EnrollCustomerCmd(customerId, "John Doe", "john@example.com", "GOV-ID-123"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new EnrollCustomerCmd(customerId, "John Doe", "john@example.com", "GOV-ID-123"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new EnrollCustomerCmd(customerId, "John Doe", "john@example.com", "GOV-ID-123"));
        aggregate.clearEvents();
    }

    @When("the command is executed with invalid email")
    public void theCommandIsExecutedWithInvalidEmail() {
        try {
            aggregate.execute(
                new UpdateCustomerDetailsCmd(
                    customerId,
                    "Jane Doe",
                    "invalid-email", // Invalid email
                    "10-20-30",
                    "1990-01-01"
                )
            );
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @When("the command is executed with empty name and dob")
    public void theCommandIsExecutedWithEmptyNameAndDob() {
        try {
            aggregate.execute(
                new UpdateCustomerDetailsCmd(
                    customerId,
                    "", // Empty name
                    "jane@example.com",
                    "10-20-30",
                    "" // Empty dob
                )
            );
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @When("the delete command is executed with active accounts")
    public void theDeleteCommandIsExecutedWithActiveAccounts() {
        try {
            aggregate.execute(new DeleteCustomerCmd(customerId, true));
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException
        );
    }
}
