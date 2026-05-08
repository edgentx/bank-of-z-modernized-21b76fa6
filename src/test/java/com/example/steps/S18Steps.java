package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-05";
    private boolean simulatedAuthStatus = true;
    private boolean simulatedSystemReady = true;
    
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-05";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        simulatedAuthStatus = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // In a real scenario, this might check the last activity timestamp against the config.
        // For this test, we simulate a state where the start is rejected due to timeout.
        // We will set a flag or use a specific command parameter that the aggregate detects.
        // However, the command takes 'isAuthenticated'. Let's rely on a hypothetical check or
        // modify the aggregate logic slightly to check a 'isTimedOut' flag if needed.
        // Since the aggregate validates on Start, let's assume the aggregate tracks a 'timeout' state.
        // Or, more simply, we enforce this logic via a check in the step or command.
        // The Scenario implies the command execution should fail.
        // Let's use the command to pass a 'timedOut' marker, or just rely on the state.
        // The most idiomatic way is that the Aggregate KNOWS it is timed out.
        aggregate.markAsTimedOut(); 
        // NOTE: The aggregate logic needs to check this state. 
        // I will add a check in the aggregate's execute method for a timed-out state if necessary.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        simulatedSystemReady = false;
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            tellerId,
            terminalId,
            simulatedAuthStatus,
            simulatedSystemReady
        );
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

}
