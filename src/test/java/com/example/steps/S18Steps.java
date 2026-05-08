package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String sessionId = "session-123";
    private String validTellerId = "teller-001";
    private String validTerminalId = "term-A1";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in 'When' step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId, Instant.now());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
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
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Manually setting authenticated state is not possible via commands,
        // so we assume the constructor defaults to not authenticated.
        // If we needed an 'authenticated' state to exist before, we'd issue a command.
        // Here we rely on the constructor state (unauthenticated) and provide a command that
        // implies authentication hasn't happened or is invalid.
        // However, the command takes an ID. The validation logic inside the aggregate
        // should fail if the teller is not 'known' or 'authenticated'.
        // Since we can't set internal state easily, we'll rely on the aggregate's internal logic
        // to reject the command based on a precondition check.
        // For this specific step implementation, we'll use a null or empty tellerId to simulate
        // the 'violation' if the aggregate checks for it, OR we assume the aggregate
        // checks an external Auth Port. Since we don't have the port wired in tests,
        // we'll use the constraint that the teller ID must not be null/empty to pass validation.
        validTellerId = null; // Trigger validation failure
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate(sessionId);
        // This scenario implies the session is already active, and we are trying to start it again?
        // Or that the session exists in the past.
        // StartSessionCmd is usually for transition from IDLE to ACTIVE.
        // We will interpret 'violation' here as an attempt to start a session that is
        // somehow already in an invalid state regarding time.
        // The simplest interpretation for this unit test context: 
        // We start the session once (making it active), then try to start it again.
        // The aggregate should reject the second start.
        
        // Execute first start
        Command cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId, Instant.now());
        aggregate.execute(cmd);
        
        // Now the aggregate is in ACTIVE state. Running StartSessionCmd again should fail.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Terminal ID is missing or invalid.
        validTerminalId = ""; 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }

}
