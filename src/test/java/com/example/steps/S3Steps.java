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
    private String customerId;
    private String emailAddress;
    private String sortCode;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customer = new CustomerAggregate("cust-123");
        customer.setEnrolled(true);
        customer.setFullName("John Doe");
        customer.setEmail("old@example.com");
        customer.setDeleted(false);
        customer.setHasActiveBankAccounts(false);
        this.customerId = "cust-123";
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_uniqueness() {
        customer = new CustomerAggregate("cust-123");
        customer.setEnrolled(true);
        customer.setFullName("Jane Doe");
        customer.setEmail("old@example.com");
        this.customerId = "cust-123";
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_not_empty() {
        customer = new CustomerAggregate("cust-123");
        customer.setEnrolled(true);
        customer.setFullName(""); // Violates Name not empty
        customer.setEmail("valid@example.com");
        this.customerId = "cust-123";
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_deletion_with_active_accounts() {
        customer = new CustomerAggregate("cust-123");
        customer.setEnrolled(true);
        customer.setFullName("Active User");
        customer.setEmail("user@example.com");
        customer.setDeleted(true);             // Is marked deleted
        customer.setHasActiveBankAccounts(true); // But has active accounts
        this.customerId = "cust-123";
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Usually matches the ID used to create the aggregate
        this.customerId = customer.id();
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        this.emailAddress = "new.address@example.com";
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        this.sortCode = "10-20-30";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customerId, emailAddress, sortCode);
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
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
        assertEquals(emailAddress, event.emailAddress());
        assertEquals(sortCode, event.sortCode());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In this domain implementation, domain errors are modeled as IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
