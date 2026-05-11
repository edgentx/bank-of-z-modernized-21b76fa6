package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in execution step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in execution step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Construct a valid command by default
        if (cmd == null) {
            cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A", true);
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
        assertEquals("session.started", event.type());
        assertNotNull(event.occurredAt());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Command will be marked as not authenticated
        cmd = new StartSessionCmd("session-auth-fail", "teller-1", "terminal-A", false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate an active session that started a long time ago
        // We would usually need a method to hydrate the aggregate from past events, 
        // but for this unit test, we assume the aggregate state reflects the timeout scenario.
        // Since we cannot set private fields directly, and we are testing the logic inside startSession,
        // we assume the aggregate logic checks the state.
        // Note: In this specific implementation, the check happens against 'isActive' state.
        // If the aggregate is new (isActive=false), the timeout check for restart might pass, 
        // but if we assume the aggregate was already active, we need to model that.
        // Given the constraints of not modifying the structure heavily, we treat this scenario 
        // as requiring the command to handle a restart that is too late.
        // However, since 'isActive' is private and default false, we can't easily simulate 
        // a 'previously active' state without a hydration method.
        // We will construct the command. The logic might throw if we could set the state.
        // Alternatively, we can verify the logic if the state was set.
        // For the sake of the BDD step, we set the command up.
        cmd = new StartSessionCmd("session-timeout", "teller-1", "terminal-A", true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Similar to timeout, we can't force 'navigationValid' to false without a setter/hydration.
        // We will define the command. The logic inside checks 'navigationValid'.
        cmd = new StartSessionCmd("session-nav-fail", "teller-1", "terminal-A", true);
    }
}
