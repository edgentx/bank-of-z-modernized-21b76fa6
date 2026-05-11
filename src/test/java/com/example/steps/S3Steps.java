package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception caughtException;
    private List events;

    // Helper to enroll a valid customer so we can update them
    private CustomerAggregate createValidAggregate(String customerId) {
        CustomerAggregate agg = new CustomerAggregate(customerId);
        // We manually apply the state change here to simulate a pre-existing enrolled customer
        // without running the full command pipeline in the 'Given' steps for simplicity, 
        // or we could execute the EnrollCustomerCmd.
        // Let's execute EnrollCustomerCmd to be consistent.
        EnrollCustomerCmd enrollCmd = new EnrollCustomerCmd(customerId, "John Doe", "john.doe@example.com", "GOV123");
        agg.execute(enrollCmd);
        agg.clearEvents(); // Clear enrollment events so we only see update events
        return agg;
    }

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        aggregate = createValidAggregate("CUST-123");
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_unique_email_and_id() {
        aggregate = createValidAggregate("CUST-INVALID");
        // The violation is contextual (uniqueness) which is passed via command flag
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        aggregate = createValidAggregate("CUST-EMPTY");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_active_accounts() {
        aggregate = createValidAggregate("CUST-ACTIVE");
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // ID is handled in the aggregate initialization or command creation
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        // Handled in command creation
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        // Handled in command creation
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        caughtException = null;
        try {
            // Scenario 1: Success
            if (aggregate.getId().equals("CUST-123")) {
                UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("CUST-123", "Jane Doe", "jane.doe@example.com", "10-20-30", true, false);
                events = aggregate.execute(cmd);
            }
            // Scenario 2: Unique Email Violation
            else if (aggregate.getId().equals("CUST-INVALID")) {
                // isEmailUnique = false
                UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("CUST-INVALID", "Jane Doe", "taken@example.com", "10-20-30", false, false);
                events = aggregate.execute(cmd);
            }
            // Scenario 3: Empty Name Violation
            else if (aggregate.getId().equals("CUST-EMPTY")) {
                // fullName is blank
                UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("CUST-EMPTY", "", "jane@example.com", "10-20-30", true, false);
                events = aggregate.execute(cmd);
            }
            // Scenario 4: Active Accounts Violation
            else if (aggregate.getId().equals("CUST-ACTIVE")) {
                // hasActiveAccounts = true
                UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd("CUST-ACTIVE", "Jane Doe", "jane@example.com", "10-20-30", true, true);
                events = aggregate.execute(cmd);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        } catch (UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        Assertions.assertNotNull(events);
        Assertions.assertFalse(events.isEmpty());
        Assertions.assertTrue(events.get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) events.get(0);
        Assertions.assertEquals("customer.details.updated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}