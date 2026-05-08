package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S4Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private boolean hasActiveAccounts;

    // Helper to hydrate the aggregate to a valid state
    private void hydrateValidCustomer(String customerId, String fullName, String email, String govtId) {
        aggregate = new CustomerAggregate(customerId);
        // We use the execute pattern to hydrate, as per feedback constraints
        aggregate.execute(new EnrollCustomerCmd(customerId, fullName, email, govtId));
        aggregate.clearEvents(); // Clear hydration events so we only test the Delete command

        // Hydrate the internal 'hasActiveAccounts' state for testing purposes
        // Note: In a real scenario, this state comes from other aggregates or queries.
        // Here we mock it by setting the flag used in the precondition check.
        if (hasActiveAccounts) {
             // Simulating internal state update for active accounts
             // Since CustomerAggregate doesn't expose a setter, we rely on the test logic flag.
             // However, the Aggregate needs to know this state. 
             // The existing CustomerAggregate doesn't have this field. 
             // We will assume the test setup reflects the state the Aggregate *would* have known if fully hydrated.
        }
    }

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        hasActiveAccounts = false; // Default to no accounts for success path
        hydrateValidCustomer("cust-123", "John Doe", "john.doe@example.com", "GOV123");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Implicit in the aggregate setup, placeholder for clarity
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("the customer has no active accounts")
    public void the_customer_has_no_active_accounts() {
        hasActiveAccounts = false;
        // If aggregate had the field, we would set it here.
        // aggregate.setActiveAccounts(false);
    }

    @Given("the customer owns active bank accounts")
    public void the_customer_owns_active_bank_accounts() {
        hasActiveAccounts = true;
        // If aggregate had the field, we would set it here.
        // aggregate.setActiveAccounts(true);
    }

    @Given("A Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_and_govt_id() {
        hasActiveAccounts = false;
        // Missing email and govtId
        aggregate = new CustomerAggregate("cust-void");
        // We don't enroll fully, just create the shell or enroll with bad data to trigger validation
        // Validation logic is inside execute, so we prepare the state.
        // However, Delete validation checks current state.
        // If we create a new Aggregate, it has empty fields.
        hydrateValidCustomer("cust-void", "Jane", null, null); // Attempting to hydrate with nulls might fail Enroll, but let's assume we just force the state.
        // Re-hydrating manually to bypass Enroll validation for the sake of testing Delete validation
        aggregate = new CustomerAggregate("cust-void"); 
        // Simulating state where data is missing/invalid (e.g. data decay or unenrolled state)
        // The acceptance criteria implies checking invariants. If the aggregate is empty, it fails invariants.
    }

    @Given("A Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        hasActiveAccounts = false;
        // Name is null/empty
        hydrateValidCustomer("cust-empty-name", "", "valid@email.com", "GOV123");
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_DeleteCustomerCmd_command_is_executed() {
        try {
            Command cmd = new DeleteCustomerCmd(aggregate.id(), hasActiveAccounts);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit one event");
        Assertions.assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        // Domain errors typically manifest as IllegalArgumentException or IllegalStateException in this pattern
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Exception should be a domain error (IllegalArgumentException or IllegalStateException)"
        );
    }
}
