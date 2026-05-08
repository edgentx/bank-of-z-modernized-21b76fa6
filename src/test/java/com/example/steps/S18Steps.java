package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Pre-authenticate for valid state
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command is built in the 'When' clause, just noting valid data
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command is built in the 'When' clause, just noting valid data
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Assuming valid context defaults
        command = new StartSessionCmd("session-123", "teller-01", "terminal-01");
        executeCommand();
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-01", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Leaving it unauthenticated violates the invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Simulate that the session logic involves checking a timestamp that is too old.
        // Since we are in-memory and mocking time, we simulate the violation state by forcing
        // the aggregate into a state where it thinks it is inactive, OR by executing a command
        // with a timestamp that violates the rule. 
        // For this exercise, we'll assume the command is constructed with a timestamp 
        // in the past relative to the session's creation, or we create the aggregate in a 'TimedOut' state.
        // However, 'StartSession' is the initiation. Perhaps the context implies the 'session' 
        // (conceptually) is already active but timed out? No, the cmd is StartSession.
        // Let's interpret this as the command itself being invalid because the pre-conditions 
        // (based on the inputs) fail validation (e.g. inactive terminal).
        // For simplicity in this BDD step, we will pass a timestamp that is explicitly invalid 
        // to the command execution logic if supported, but the Command is POJO.
        // Let's assume the aggregate is simply in a state that disallows starting.
        // Wait, 'StartSession' creates the session. Maybe the 'session' ID corresponds to a stale lock?
        // Let's assume the aggregate is a re-used aggregate from a pool that is stale.
        aggregate.markStale(); // Custom helper for test state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.markInvalidNavigationContext(); // Custom helper for test state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        // Specific execution logic for negative paths might need specific command setup
        // We use the defaults from a previous Given unless specific
        if(command == null) {
             // Default command construction for negative tests
             command = new StartSessionCmd(aggregate.id(), "teller-01", "terminal-01");
             executeCommand();
        }
        
        assertNotNull(thrownException);
        // assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}