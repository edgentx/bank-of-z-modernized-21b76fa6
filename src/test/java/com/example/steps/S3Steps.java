package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainException;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private String customerId;
    private String email;
    private String sortCode;
    private String fullName;
    private String govId;
    private Instant dob;
    private boolean hasActiveAccounts;

    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        // Enroll the customer first to make it valid
        customer.execute(new EnrollCustomerCmd(customerId, "Original Name", "original@example.com", "GOV123"));
        customer.clearEvents(); // Clear enrollment events to isolate Update command events
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        customerId = "cust-123";
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        email = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        sortCode = "123456";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        // Defaults for a successful update if not overridden by violation context
        if (fullName == null) fullName = "Updated Name";
        if (dob == null) dob = Instant.now().minusSeconds(100000);
        if (govId == null) govId = "GOV123";
        if (email == null) email = "valid@example.com";
        
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
            customerId, fullName, email, sortCode, govId, dob, hasActiveAccounts
        );
        
        try {
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals(customerId, event.aggregateId());
        assertEquals(fullName, event.fullName());
        assertEquals(email, event.emailAddress());
        assertEquals(sortCode, event.sortCode());
    }

    // --- Failure Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_and_gov_id() {
        // Setup valid aggregate first
        a_valid_Customer_aggregate();
        a_valid_customerId_is_provided();
        // Set invalid data for the specific violation
        this.email = "invalid-email"; // Missing @
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        a_valid_Customer_aggregate();
        a_valid_customerId_is_provided();
        this.fullName = ""; // Empty name
        this.dob = null; // Missing DOB
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_active_accounts() {
        a_valid_Customer_aggregate();
        a_valid_customerId_is_provided();
        this.hasActiveAccounts = true; // Has active accounts
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof DomainException, "Expected DomainException, got " + caughtException.getClass().getSimpleName());
    }
}
