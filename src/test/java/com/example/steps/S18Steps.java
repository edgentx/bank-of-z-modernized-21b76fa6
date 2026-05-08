package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-123";
        // Initialize aggregate with valid defaults, but NOT started
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // In a real scenario, this might involve loading a session that failed auth
        // Here we create one and assume the command carries the auth context which is invalid
        aggregate = new TellerSessionAggregate("TS-UNAUTH");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-TIMEOUT");
        // Simulate a state where the session is considered stale/timeout immediately
        // In a real app, we might set a 'lastActivityAt' in the past, but here we rely on the specific command execution logic
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("TS-NAV-ERR");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup for the command
        // We assume the command constructed in 'When' uses valid IDs unless specified otherwise by context
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup for the command
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        String sessionId = aggregate.id();
        String tellerId = "TELLER-001";
        String terminalId = "TERM-3270-A";
        boolean isAuthenticated = true; // Default to true for the generic 'When'
        boolean isActive = true;
        boolean isNavValid = true;

        // Modify flags based on the Given context inferred from aggregate ID or state
        if (sessionId.equals("TS-UNAUTH")) {
            isAuthenticated = false;
        } else if (sessionId.equals("TS-TIMEOUT")) {
            isActive = false;
        } else if (sessionId.equals("TS-NAV-ERR")) {
            isNavValid = false;
        }

        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, isActive, isNavValid);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("TS-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on implementation, this could be IllegalStateException, IllegalArgumentException, or a custom DomainError
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
