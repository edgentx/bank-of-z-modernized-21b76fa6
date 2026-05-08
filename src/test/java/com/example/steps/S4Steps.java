package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Hydrate with valid data
        aggregate.hydrate(
                "John Doe",
                "john.doe@example.com",
                "GOV-ID-123",
                Instant.parse("1990-01-01T00:00:00Z"),
                true,
                false // no active accounts
        );
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId is implicit in the aggregate construction above
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        aggregate = new CustomerAggregate("cust-invalid");
        aggregate.hydrate(
                "Jane Doe",
                "invalid-email", // invalid email
                null, // missing govid
                Instant.now(),
                true,
                false
        );
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("cust-empty");
        aggregate.hydrate(
                null, // missing name
                "valid@example.com",
                "GOV-ID-999",
                null, // missing dob
                true,
                false
        );
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-active");
        aggregate.hydrate(
                "Rich User",
                "rich@example.com",
                "GOV-ID-888",
                Instant.now(),
                true,
                true // Has active accounts
        );
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        Command cmd = new DeleteCustomerCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("customer.deleted", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect either an IllegalArgumentException or IllegalStateException depending on the specific invariant violated.
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
                "Expected domain exception, got: " + capturedException.getClass().getSimpleName());
    }
}
