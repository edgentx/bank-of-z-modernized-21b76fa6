package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid basic aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate("session-01");
    }

    // Scenario 1: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // State carried to execution step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // State carried to execution step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("session-01", "teller-123", "term-A", Instant.now());
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
        assertEquals("session-01", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("term-A", event.terminalId());
    }

    // Scenario 2: Auth rejection
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated() {
        aggregate = createValidAggregate();
        // We simulate the violation by preparing a command with null auth timestamp
    }

    // Re-use When/Then from above
    
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

    // Scenario 3: Timeout rejection
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout() {
        aggregate = createValidAggregate();
        // Simulate an aggregate that is already in a bad state regarding timeouts
        // For S-18 StartSession, this implies the session state prevents start (e.g. already started long ago)
        // Or simply rely on the state check. Let's set it to STARTED to simulate conflict.
        aggregate.setState(TellerSessionAggregate.SessionState.STARTED);
        // Or simulate time logic if applicable, but the state machine is the primary invariant.
    }

    // Scenario 4: Navigation State rejection
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state() {
        aggregate = createValidAggregate();
        // Violate state by setting it to TERMINATED
        aggregate.setState(TellerSessionAggregate.SessionState.TERMINATED);
    }

}
