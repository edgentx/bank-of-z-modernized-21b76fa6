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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to enroll a customer so we can update them
    private CustomerAggregate getValidAggregate() {
        // Directly creating a valid aggregate state (simulating prior enrollment)
        // Or using the Enroll command if available. For unit test speed, we simulate state.
        var agg = new CustomerAggregate("cust-123");
        // We assume enrollment logic succeeded prior to these scenarios.
        // In a real test, we might call execute(new EnrollCustomerCmd(...)).
        // But here, we rely on the constructor to set the ID, and we might need to 'hydrate' it manually if we don't run Enroll.
        // However, the execute method checks `enrolled` flag.
        // Since we don't have a public hydrate/setEnrolled, we MUST run Enroll.
        try {
            agg.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
                "cust-123", "John Doe", "john.doe@example.com", "GOV-ID-123"
            ));
        } catch (Exception e) {
            fail("Setup failed: Could not enroll customer", e);
        }
        return agg;
    }

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        aggregate = getValidAggregate();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_and_gov_id() {
        aggregate = getValidAggregate();
        // State violation happens in the command execution, but we can set up the command next.
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        aggregate = getValidAggregate();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_active_accounts() {
        aggregate = getValidAggregate();
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Implicitly handled by the aggregate setup, commands will use this ID.
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        // Handled in the When step via command construction
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        // Handled in the When step via command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        // Default valid data for "Success" scenario
        executeCommand("Jane Doe", "jane.doe@example.com", "sort-123", false, "GOV-ID-123");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email and gov id")
    public void the_UpdateCustomerDetailsCmd_command_is_executed_with_invalid_data() {
        // Scenario: A customer must have a valid, unique email address and government-issued ID.
        // Invalid email
        executeCommand("Jane Doe", "invalid-email", "sort-123", false, null); // null gov ID
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name and dob")
    public void the_UpdateCustomerDetailsCmd_command_is_executed_with_missing_data() {
        // Scenario: Customer name and date of birth cannot be empty.
        executeCommand("", "valid@example.com", "sort-123", false, "GOV-ID-123", null);
    }

    @When("the UpdateCustomerDetailsCmd command is executed with active accounts flag true")
    public void the_UpdateCustomerDetailsCmd_command_is_executed_with_active_accounts() {
        // Scenario: A customer cannot be deleted if they own active bank accounts.
        executeCommand("Jane Doe", "jane@example.com", "sort-123", true, "GOV-ID-123");
    }

    private void executeCommand(String name, String email, String sortCode, boolean hasActiveAccounts, String govId) {
        executeCommand(name, email, sortCode, hasActiveAccounts, govId, LocalDate.of(1990, Month.JANUARY, 1));
    }

    private void executeCommand(String name, String email, String sortCode, boolean hasActiveAccounts, String govId, LocalDate dob) {
        caughtException = null;
        try {
            var cmd = new UpdateCustomerDetailsCmd(aggregate.id(), name, email, dob, sortCode, hasActiveAccounts, govId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent, "Event type mismatch");

        var event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals("customer.details.updated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception but none was thrown");
        // Verify it's one of the expected domain exceptions (IllegalStateException or IllegalArgumentException)
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Exception should be a domain error (IAE or ISE)"
        );
    }
}
