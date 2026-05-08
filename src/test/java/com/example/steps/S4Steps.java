package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.CustomerDeletedEvent;
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
    private String customerId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        this.customerId = "cust-123";
        this.aggregate = new CustomerAggregate(customerId);
        // Hydrate with valid data to pass invariants
        aggregate.hydrate("John Doe", "john@example.com", "GOV-ID-123", Instant.now().minusSeconds(100000), false);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        this.customerId = "cust-invalid-email";
        this.aggregate = new CustomerAggregate(customerId);
        // Missing email and Gov ID
        aggregate.hydrate("Jane Doe", null, null, Instant.now().minusSeconds(100000), false);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        this.customerId = "cust-invalid-name-dob";
        this.aggregate = new CustomerAggregate(customerId);
        // Missing Name and DOB
        aggregate.hydrate(null, "jane@example.com", "GOV-ID-456", null, false);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        this.customerId = "cust-active-accounts";
        this.aggregate = new CustomerAggregate(customerId);
        // Has active accounts
        aggregate.hydrate("Active User", "active@example.com", "GOV-ID-789", Instant.now().minusSeconds(100000), true);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId is already set in the Given steps
        assertNotNull(customerId);
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(customerId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDeletedEvent);
        assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Expected domain error (IAE or ISE), got: " + caughtException.getClass().getSimpleName());
    }
}
