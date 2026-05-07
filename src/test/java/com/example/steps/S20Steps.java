package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<?> resultEvents;
    private Exception capturedException;

    // --- Scenarios ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("sess-123");
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setCurrentState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-invalid-auth");
        // Do not mark authenticated
        aggregate.setCurrentState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-timeout");
        aggregate.markAuthenticated();
        aggregate.setCurrentState("IDLE");
        // Set last activity to 20 minutes ago (violating 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("sess-bad-state");
        aggregate.markAuthenticated();
        aggregate.setCurrentState("IN_TRANSACTION"); // Not IDLE
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicitly handled by the aggregate instance ID in this setup
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent, "Event should be TellerSessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // In Java Domain-Driven Design, invariants are often enforced via IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException for domain error");
    }
}
