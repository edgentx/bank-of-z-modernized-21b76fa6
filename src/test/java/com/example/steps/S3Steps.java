package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Aggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private UpdateCustomerDetailsCmd lastCommand;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // Default constructor sets ID. We assume state is loaded or initialized.
        this.aggregate = new CustomerAggregate("cust-123");
        // Manually setting internal state for test purposes (simulating a loaded aggregate)
        // In a real scenario, we might use a repository to load a fully built aggregate.
        // Here we rely on the constructor and potentially a reflectionless setup or just the command logic.
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in command creation
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in command creation
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in command creation
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        executeCommand(new UpdateCustomerDetailsCmd("cust-123", "new@example.com", "123456", null, null, true, false));
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(aggregate);
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof CustomerDetailsUpdatedEvent);
        var evt = (CustomerDetailsUpdatedEvent) events.get(0);
        assertEquals("new@example.com", evt.emailAddress());
        assertEquals("customer.details.updated", evt.type());
    }

    // --- Rejection Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesValidEmail() {
        this.aggregate = new CustomerAggregate("cust-violate-email");
        // We simulate the violation by passing a bad email in the command step
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesEmptyName() {
        this.aggregate = new CustomerAggregate("cust-violate-name");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        this.aggregate = new CustomerAggregate("cust-violate-delete");
    }

    // Specific When handlers for the violation scenarios to construct the correct bad data
    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theCommandIsExecutedWithInvalidEmail() {
        executeCommand(new UpdateCustomerDetailsCmd("cust-violate-email", "invalid-email", "123456", null, null, true, false));
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theCommandIsExecutedWithEmptyName() {
        executeCommand(new UpdateCustomerDetailsCmd("cust-violate-name", "valid@example.com", "123456", "", null, true, false));
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theCommandIsExecutedWithActiveAccounts() {
        // Simulating a delete attempt (active = false) while having active accounts (hasActiveAccounts = true)
        executeCommand(new UpdateCustomerDetailsCmd("cust-violate-delete", "valid@example.com", "123456", null, null, false, true));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // The aggregate throws IllegalArgumentException or IllegalStateException for domain violations
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    private void executeCommand(UpdateCustomerDetailsCmd cmd) {
        this.lastCommand = cmd;
        this.capturedException = null;
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }
}
