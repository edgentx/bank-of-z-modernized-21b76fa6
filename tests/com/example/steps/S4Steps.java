package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-1");
        // Hydrate with valid data to pass invariants
        aggregate.hydrate(
            "John Doe", 
            "john.doe@example.com", 
            "GOV-ID-123", 
            false // No active accounts
        );
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        aggregate = new CustomerAggregate("cust-2");
        // Hydrate with invalid data (bad email, missing ID)
        aggregate.hydrate(
            "Jane Doe",
            "invalid-email",
            null,
            false
        );
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("cust-3");
        // Hydrate with invalid data (empty name)
        aggregate.hydrate(
            null, // Name is null
            "valid@example.com",
            "GOV-ID-456",
            false
        );
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-4");
        // Hydrate with valid personal data, BUT has active accounts
        aggregate.hydrate(
            "Active User",
            "active@example.com",
            "GOV-ID-789",
            true  // <-- Active accounts flag is true
        );
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // The aggregate is already initialized with an ID in the Given steps.
        // This step confirms context for the scenario.
        assertNotNull(aggregate.id());
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("customer.deleted", resultEvents.get(0).type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect either IAE or ISE depending on the specific invariant violated
        assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException,
            "Expected domain error, but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
