package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the When step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the When step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-123", "term-456");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "First event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We simulate a violation by attempting to start a session without prior authentication state.
        // However, since the aggregate is new, we can just test the negative invariant logic if it existed.
        // For this scenario, we will assume the command logic handles auth, or we can manually set a state
        // if the aggregate allowed it. Given the current aggregate, we just test execution.
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // If the aggregate required an 'authenticated' flag to be true before starting,
        // we would leave it false (default).
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Inactivity is usually checked on read or subsequent commands, not necessarily on Start.
        // We will construct the aggregate normally.
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        // Context validity.
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        // In a real scenario with complex invariants, we might catch IllegalStateException.
        // Given the happy-path implementation, we check if any exception occurred.
        // If the previous code didn't throw, this test would fail, revealing missing invariant logic.
        Assertions.assertNotNull(thrownException, "Expected an exception but command succeeded");
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Additional context hook for violating tests to force a failure if logic is missing
    // (Optional, depending on how strict we want the BDD to be on empty aggregates)
}