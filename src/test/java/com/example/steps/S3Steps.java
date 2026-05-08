package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private String emailAddress;
    private String sortCode;
    private String fullName;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        this.customerId = "cust-123";
        this.aggregate = new CustomerAggregate(customerId);
        // Pre-enroll to make it valid for updates (simulating state loaded from DB)
        aggregate.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
                customerId, "Old Name", "old@example.com", "GOV-ID-123"
        ));
        aggregate.clearEvents(); // Clear enrollment events for test clarity
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        this.customerId = "cust-123";
    }

    @Given("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        this.emailAddress = "new@example.com";
    }

    @Given("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        this.sortCode = "123456";
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailUniqueness() {
        this.customerId = "cust-invalid-email";
        this.emailAddress = "invalid-email-format"; // Violation: bad format
        this.fullName = "Valid Name";
        this.sortCode = "123456";
        this.aggregate = new CustomerAggregate(customerId);
        // Ensure it exists in DB
        aggregate.execute(new com.example.domain.customer.model.EnrollCustomerCmd(customerId, "Valid Name", "old@example.com", "GOV"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameRequired() {
        this.customerId = "cust-invalid-name";
        this.fullName = ""; // Violation: blank name
        this.emailAddress = "valid@example.com";
        this.sortCode = "123456";
        this.aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new com.example.domain.customer.model.EnrollCustomerCmd(customerId, "Valid Name", "old@example.com", "GOV"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesDeletedStatus() {
        this.customerId = "cust-deleted";
        this.fullName = "Deleted Guy";
        this.emailAddress = "deleted@example.com";
        this.sortCode = "000000";
        this.aggregate = new CustomerAggregate(customerId);
        // Simulate a deleted customer
        aggregate.markAsDeleted();
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateCustomerDetailsCmd(
                    this.customerId,
                    this.fullName,
                    this.emailAddress,
                    this.sortCode
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals(customerId, event.aggregateId());
        assertEquals(emailAddress, event.emailAddress());
        assertEquals(fullName, event.fullName());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Domain errors are modeled as IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
