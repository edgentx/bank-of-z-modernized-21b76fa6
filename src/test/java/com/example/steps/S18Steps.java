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
    private String validTellerId = "TELLER_01";
    private String validTerminalId = "TERM_42";
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        capturedException = null;
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup: stored in field for When step
        assertNotNull(validTellerId);
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup: stored in field for When step
        assertNotNull(validTerminalId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Construct command with defaults for valid case
            Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId, true);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(validTellerId, event.getTellerId());
        assertEquals(validTerminalId, event.getTerminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        // We simulate this violation by providing null/blank IDs in the When step logic
        validTellerId = null; 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        // Force aggregate into a state that represents an expired session context
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        // We use the flag in the Command to signal this invalid context
        validTerminalId = validTerminalId + "_INVALID_CTX";
    }

    // Custom When handling for negative flows based on state setup above
n    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_negative() {
        try {
            // Check for the specific violation pattern to determine command validity flags
            boolean isValidContext = !validTerminalId.endsWith("_INVALID_CTX");
            
            Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId.replace("_INVALID_CTX", ""), isValidContext);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but none was");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException, 
            "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + capturedException.getClass().getSimpleName());
    }
}
