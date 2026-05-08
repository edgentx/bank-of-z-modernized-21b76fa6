package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.SessionStartedEvent;
import com.example.domain.tellermgmt.model.StartSessionCmd;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import com.example.domain.tellermgmt.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // In-memory repository logic would be injected or mocked, but we instantiate directly for this unit-level BDD
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = repository.create("session-123");
        this.caughtException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When step by constructing the command with valid IDs
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                "session-123",
                "teller-42",
                "terminal-T9",
                true,   // authenticated
                false,  // timedOut
                "HOME"  // navState
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("terminal-T9", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        this.aggregate = repository.create("session-auth-fail");
        // The aggregate state might be fine, but the Command carries the auth context for this scenario
        // or the aggregate is set to unauthenticated. 
    }

    // Overload the When step for violation scenarios using specific parameters
    @When("the StartSessionCmd command is executed with unauthenticated context")
    public void the_StartSessionCmd_command_is_executed_unauthenticated() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                "session-auth-fail", "teller-42", "terminal-T9", 
                false, // authenticated = false
                false, "HOME"
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout() {
        this.aggregate = repository.create("session-timeout");
    }

    @When("the StartSessionCmd command is executed with timed out context")
    public void the_StartSessionCmd_command_is_executed_timed_out() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                "session-timeout", "teller-42", "terminal-T9", 
                true, 
                true, // timedOut = true
                "HOME"
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect() {
        this.aggregate = repository.create("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_StartSessionCmd_command_is_executed_invalid_nav() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                "session-nav-fail", "teller-42", "terminal-T9", 
                true, 
                false, 
                "" // Invalid nav state
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We check for RuntimeException or specific domain errors
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Inner class for test isolation
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate;
        }

        @Override
        public TellerSessionAggregate create(String id) {
            return new TellerSessionAggregate(id);
        }

        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.empty();
        }
    }
}
