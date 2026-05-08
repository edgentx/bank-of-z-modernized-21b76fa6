package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDeletedEvent;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate customer;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        customer = new CustomerAggregate("cust-123");
        customer.setFullName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setGovernmentId("GOV-ID-123");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1).toString());
        customer.setEnrolled(true);
        customer.setActiveAccountCount(0); // No active accounts
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Implicitly handled by the aggregate ID in setup
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_with_invalid_email_and_gov_id() {
        customer = new CustomerAggregate("cust-invalid");
        customer.setFullName("Jane Doe");
        customer.setEmail("invalid-email"); // Invalid email
        customer.setGovernmentId(""); // Invalid Gov ID
        customer.setDateOfBirth("1985-05-05");
        customer.setActiveAccountCount(0);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_with_empty_name_and_dob() {
        customer = new CustomerAggregate("cust-empty-fields");
        customer.setFullName(""); // Empty Name
        customer.setEmail("valid@example.com");
        customer.setGovernmentId("GOV-999");
        customer.setDateOfBirth(""); // Empty DOB
        customer.setActiveAccountCount(0);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_with_active_accounts() {
        customer = new CustomerAggregate("cust-accounts");
        customer.setFullName("Rich User");
        customer.setEmail("rich@example.com");
        customer.setGovernmentId("GOV-RICH");
        customer.setDateOfBirth("1980-01-01");
        customer.setActiveAccountCount(5); // Has active accounts
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_delete_customer_cmd_command_is_executed() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(customer.id());
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "One event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof CustomerDeletedEvent, "Event should be CustomerDeletedEvent");
        assertTrue(customer.isDeleted(), "Aggregate should be marked as deleted");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Exception should be a domain error (IllegalArgumentException or IllegalStateException)");
    }
}
