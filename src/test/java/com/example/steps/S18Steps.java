package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Assume default valid state for generic 'valid' scenario
        // We set specific values in the And steps
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-TN3270-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, Instant.now().plusSeconds(300));
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals(tellerId, event.tellerId());
        Assertions.assertEquals(terminalId, event.terminalId());
    }

    // --- Violations Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.sessionId = "sess-auth-violation";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // In a real scenario, we might load a state where the teller is NOT authenticated.
        // For this aggregate, the command carries the auth status.
        this.tellerId = "unauthenticated-teller";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout-violation";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "teller-timeout";
        this.terminalId = "term-01";
        // The violation is triggered by the command parameters (configured timeout)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.sessionId = "sess-nav-violation";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "teller-nav";
        this.terminalId = "term-02";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException or IllegalStateException)");
    }

    @Given("a valid TellerSession aggregate")
    public void resetAggregate() {
        a_valid_teller_session_aggregate();
    }

    @When("the StartSessionCmd command is executed")
    public void executeCmd() {
        the_start_session_cmd_command_is_executed();
    }

    @Then("a session.started event is emitted")
    public void verifyEvent() {
        a_session_started_event_is_emitted();
    }
}
