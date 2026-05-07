package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSession aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSession("session-123", "teller-456");
        // Simulate a session that is active and authenticated
        // We must manually set state here because the aggregate uses package-private setters or we rely on the constructor.
        // For the purpose of testing commands, we assume the aggregate is hydrated in a valid state.
        // The constructor creates a session in AUTHENTICATED state.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by aggregate creation in previous step
        // The command created in the When step will use this ID.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Create a session that is NOT authenticated
        this.aggregate = new TellerSession("session-violation-1", null);
        // Force state to UNAUTHENTICATED
        this.aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSession("session-timeout", "teller-1");
        // Simulate a session that has timed out
        this.aggregate.forceTimeoutState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSession("session-nav-bad", "teller-1");
        // Simulate a corrupted navigation state
        this.aggregate.corruptNavigationState();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd("session-123", "teller-456");
            // Adjust ID for specific violation scenarios if needed, or use generic ID matching aggregate
            if (aggregate.id().startsWith("session-violation")) {
                cmd = new EndSessionCmd(aggregate.id(), null);
            } else if (aggregate.id().startsWith("session-timeout")) {
                cmd = new EndSessionCmd(aggregate.id(), "teller-1");
            } else if (aggregate.id().startsWith("session-nav-bad")) {
                cmd = new EndSessionCmd(aggregate.id(), "teller-1");
            }
            
            this.resultEvents = aggregate.execute(cmd);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // Specific checks for error types could be added here
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected an IllegalStateException or IllegalArgumentException, got " + capturedException.getClass().getSimpleName()
        );
    }
}
