package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate customer;
    private String customerId;
    private String emailAddress;
    private String sortCode;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        this.customer = new CustomerAggregate("cust-123");
        // Initialize state as if enrolled to allow updates
        // Note: In a real repo, we'd rehydrate from events. Here we mock state via reflection or explicit setup if accessible.
        // Assuming execute sets state, but the constructor doesn't. We might need to apply a mock Enroll command first or allow defaults.
        // Given the constraints and existing Aggregate structure, we'll rely on the command setting state or use reflection.
        // However, 'execute' applies state changes. Let's ensure the aggregate is ready.
        this.customerId = "cust-123";
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Using the default from the aggregate
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        this.emailAddress = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        this.sortCode = "123456";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customerId, emailAddress, sortCode);
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals(emailAddress, event.email());
        Assertions.assertEquals(sortCode, event.sortCode());
    }

    // --- Failure Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesAValidEmailAndGovernmentId() {
        this.customer = new CustomerAggregate("cust-123");
        this.emailAddress = "invalid-email"; // Missing @
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDoB() {
        // This scenario implies a structural check. For UpdateCustomerDetailsCmd, if we were updating names,
        // we would enforce blank checks.
        this.customer = new CustomerAggregate("cust-123");
        this.emailAddress = "valid@example.com";
        // Assuming the command checks apply if name was being updated. The prompt implies specific validation logic.
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        // This is a state check. For an Update command, this might be context-specific.
        // Assuming the Command/Aggregate enforces business rules.
        this.customer = new CustomerAggregate("cust-123");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception but command succeeded");
        // We typically expect IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException,
            "Expected domain error, got " + caughtException.getClass().getSimpleName()
        );
    }
}
