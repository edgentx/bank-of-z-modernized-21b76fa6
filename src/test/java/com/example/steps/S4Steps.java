package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    // --- Givens ---

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        customerId = "cust-" + UUID.randomUUID();
        // Enroll a valid customer first to establish state
        aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new EnrollCustomerCmd(customerId, "John Doe", "john.doe@example.com", "GOV123"));
        // Clear events from enrollment so we only inspect the Delete events later
        aggregate.clearEvents();
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        assertNotNull(customerId);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_id() {
        customerId = "cust-invalid-id";
        aggregate = new CustomerAggregate(customerId);
        // Enroll with bad data to simulate the invariant violation state (assuming aggregate allows enrollment then checks delete)
        // Or directly manipulate if the aggregate doesn't expose mutators. 
        // Here we rely on the Command logic. We'll simulate a 'bad' aggregate by enrolling with bad data 
        // or handling the 'missing' data scenario in the setup.
        // Since the existing Enrollment logic enforces validity, we assume the 'violation' refers to the delete context 
        // OR that the aggregate somehow has invalid state (e.g., legacy data).
        // For this test, we'll pass a valid aggregate but assume the *invariant* checked during delete fails.
        // However, the scenario implies the aggregate *is* the invalid state.
        // We will use the aggregate state.
        aggregate.execute(new EnrollCustomerCmd(customerId, "Jane", "invalid-email", null));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_and_dob() {
        customerId = "cust-invalid-name";
        aggregate = new CustomerAggregate(customerId);
        // Create a customer with blank name
        aggregate.execute(new EnrollCustomerCmd(customerId, "", "valid@example.com", "ID123"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        customerId = "cust-active-accounts";
        aggregate = new CustomerAggregate(customerId);
        aggregate.execute(new EnrollCustomerCmd(customerId, "Active User", "active@example.com", "ID456"));
        aggregate.clearEvents();
    }

    // --- Whens ---

    @When("the DeleteCustomerCmd command is executed")
    public void the_delete_customer_cmd_command_is_executed() {
        Command cmd = new DeleteCustomerCmd(customerId);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        assertNotNull(resultingEvents, "Resulting events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Expected at least one event");
        assertTrue(resultingEvents.get(0) instanceof CustomerDeletedEvent, "Expected CustomerDeletedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors are typically IllegalArgumentException or IllegalStateException
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, got " + caughtException.getClass().getSimpleName()
        );
    }
}
