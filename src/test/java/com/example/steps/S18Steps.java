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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    // Helper to create a fresh valid aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate("session-123");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = createValidAggregate();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup for the command, handled in 'When'
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup for the command, handled in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Assuming valid context defaults to "teller-1" and "term-1" for simplicity
        executeCommand("teller-1", "term-1", true, Instant.now().plus(Duration.ofHours(8)), true);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    // --- Scenarios for Rejections ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = createValidAggregate();
        // Auth status is passed in the command for this story's context
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        aggregate = createValidAggregate();
        // Timeout config is passed in the command
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = createValidAggregate();
        // Nav state validity is passed in the command
    }

    // Reusing the When step, but we can override parameters if needed via context storage.
    // For simplicity in BDD mapping, we assume specific violation scenarios map to specific command params.

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        executeCommand("teller-1", "term-1", false, Instant.now().plus(Duration.ofHours(8)), true);
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_with_invalid_timeout() {
        executeCommand("teller-1", "term-1", true, Instant.now().minusSeconds(60), true);
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_StartSessionCmd_command_is_executed_with_invalid_nav_state() {
        executeCommand("teller-1", "term-1", true, Instant.now().plus(Duration.ofHours(8)), false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    private void executeCommand(String tellerId, String terminalId, boolean isAuthenticated, Instant timeoutAt, boolean isNavStateValid) {
        thrownException = null;
        resultEvents = null;
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId, isAuthenticated, timeoutAt, isNavStateValid);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
