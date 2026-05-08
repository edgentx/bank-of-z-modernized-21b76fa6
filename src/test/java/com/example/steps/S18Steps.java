package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId;
    private String terminalId;
    private boolean authenticated;
    private Instant lastActivityAt;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-01";
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-01";
        this.authenticated = false; // Violation: not authenticated
        this.lastActivityAt = Instant.now();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-01";
        this.authenticated = true;
        // Violation: last activity was 31 minutes ago (assuming timeout is 30)
        this.lastActivityAt = Instant.now().minusSeconds(31 * 60);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Create an aggregate and manually put it in a started state to violate the 'NONE' precondition
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force internal state (simulating a session that already exists)
        // In a real repo, we'd load an existing aggregate. Here we simulate by reusing or manual manipulation.
        // Since TellerSessionAggregate has no public setters for state, we assume the 'Given' implies
        // we are trying to start a session on an aggregate that is already active.
        // To achieve this in the test without reflection, we might need a Test-specific builder or
        // simply accept that this scenario tests the state check logic.
        // However, since we can't set state to STARTED without executing the command (which would fail),
        // we will simulate this by checking the logic path. 
        // A clean way for this BDD: The aggregate defaults to NONE. 
        // To violate the context, we pretend the aggregate was previously started.
        // Since I cannot modify the aggregate for test purposes, and I cannot modify the domain to expose setters,
        // I will rely on the fact that the 'execute' method checks state.
        // But how to get it into STARTED state? I can't with the public API.
        // I will interpret this scenario as: The system state implies a conflict.
        // Actually, if I run the command twice, the second time it will fail.
        // So for this step, I'll set up the valid context, and maybe the 'context' violation is about the aggregate logic.
        // Let's assume the violation is passed via the command parameters or implicit state.
        // Wait, the scenario says "aggregate that violates". This implies the aggregate is in a bad state.
        // Since I can't put it in a bad state easily, I will setup a valid one, and the step definition implies
        // that we might be handling a pre-existing session.
        // Let's try to execute a valid command first to get it started, then the 'When' will be the second command.
        
        // Re-initializing valid params to start it first
        this.tellerId = "teller-01";
        this.terminalId = "term-01";
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
        
        // Pre-existing session violation: We start it here.
        var cmd = new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, lastActivityAt);
        aggregate.execute(cmd); // Now state is STARTED
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            var cmd = new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, lastActivityAt);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        
        // Also verify aggregate state if possible
        assertEquals(TellerSessionAggregate.State.STARTED, aggregate.getState());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        // Optionally check the message matches the specific invariant
        // System.out.println(thrownException.getMessage());
    }
}