package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customer = new CustomerAggregate("cust-1");
        // Simulate enrollment so the customer is in a valid state for updates
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-1", 
            "John Doe", 
            "john.doe@example.com", 
            "GOV-ID-123"
        ));
        customer.clearEvents(); // Clear enrollment events to isolate test
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_with_invalid_email() {
        customer = new CustomerAggregate("cust-2");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-2", "Jane Doe", "jane@example.com", "GOV-ID-456"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_with_empty_name_dob() {
        customer = new CustomerAggregate("cust-3");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-3", "Existing Name", "existing@example.com", "GOV-ID-789"));
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_with_active_accounts() {
        customer = new CustomerAggregate("cust-4");
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
            "cust-4", "Risk Taker", "risk@example.com", "GOV-ID-RISK"));
        customer.clearEvents();
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Id is implicit in the aggregate instance
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        // Handled in the When step via command construction
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        // Note: sortCode is not in the domain model provided, focusing on email/name/dob
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        // Default success case values
        String newEmail = "updated@example.com";
        String newName = "Updated Name";
        String newDob = "1990-01-01";

        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customer.id(), newEmail, newName, newDob);
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void the_update_command_is_executed_with_invalid_email() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customer.id(), "invalid-email", "Jane Doe", "1980-01-01");
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name")
    public void the_update_command_is_executed_with_empty_name() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customer.id(), "valid@example.com", "", "1980-01-01");
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_DeleteCustomerCmd_command_is_executed() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(customer.id(), true); // hasActiveAccounts = true
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertEquals("customer.details.updated", resultingEvents.get(0).type());
        assertEquals("updated@example.com", customer.getEmail());
        assertEquals("Updated Name", customer.getFullName());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
