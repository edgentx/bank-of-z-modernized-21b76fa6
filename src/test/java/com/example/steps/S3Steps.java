package com.example.steps;

import com.example.domain.customer.command.UpdateCustomerDetailsCmd;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        // Initialize with valid state for the positive path
        aggregate = new CustomerAggregate("customer-123");
        aggregate.setFullName("John Doe");
        aggregate.setGovernmentId("GOV123");
        aggregate.setEnrolled(true);
        // Sort code and email are initially null/empty to be set by update
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_with_invalid_email_and_gov_id() {
        aggregate = new CustomerAggregate("customer-invalid");
        aggregate.setFullName("Jane Doe");
        aggregate.setGovernmentId(null); // Violation: No Gov ID
        // Email validation is handled in the command execution for the new value
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_with_empty_name() {
        aggregate = new CustomerAggregate("customer-no-name");
        aggregate.setFullName(null); // Violation: Empty name
        aggregate.setGovernmentId("GOV456");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_with_active_accounts() {
        aggregate = new CustomerAggregate("customer-locked");
        aggregate.setFullName("Locked User");
        aggregate.setGovernmentId("GOV789");
        aggregate.setHasActiveBankAccounts(true); // Simulate state
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // ID is usually part of the aggregate identity, implicit in the setup above.
        // For command execution, we ensure the command targets the correct aggregate.
        if (aggregate == null) {
            throw new RuntimeException("Aggregate not initialized");
        }
    }

    @Given("a valid emailAddress is provided")
    public void a_valid_email_address_is_provided() {
        // Context stored for the command execution
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Context stored for the command execution
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_update_customer_details_cmd_command_is_executed() {
        try {
            // Simulating inputs. In a real test, these might come from scenario context.
            // The context here determines which branch we are testing.
            String email = "test@example.com";
            String sortCode = "123456";

            // Adjust data based on the scenario context setup
            if (aggregate.getId().equals("customer-invalid")) {
                // Scenario 2: Invalid email provided in command
                email = "invalid-email"; 
            }
            
            // Create command with strictly 3 arguments as defined in command package
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(aggregate.id(), email, sortCode);
            
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultingEvents, "Expected events to be emitted");
        assertFalse(resultingEvents.isEmpty(), "Expected at least one event");
        assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent, "Expected CustomerDetailsUpdatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException, 
            "Expected domain error (IllegalArgumentException or IllegalStateException)");
    }
}
