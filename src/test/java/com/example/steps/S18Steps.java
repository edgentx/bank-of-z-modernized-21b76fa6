package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> result;
    private Exception caughtException;

    private final String SESSION_ID = "session-123";
    private final String VALID_TELLER_ID = "teller-01";
    private final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup pre-conditions for a valid start: Authenticated + IDLE + Fresh Timestamp
        aggregate.markAuthenticated();
        aggregate.setNavState("IDLE");
        aggregate.setLastActivityAt(Instant.now()); 
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Intentionally not calling markAuthenticated() - defaults to false
        aggregate.setNavState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.setNavState("IDLE");
        // Simulate a session that timed out (e.g., 20 mins ago)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200)); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        // Simulate incorrect state, e.g., already navigating deep menu
        aggregate.setNavState("MENU_OPEN");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Values are constants, no action needed here unless logic depends on external context
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Values are constants
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should emit one event");
        assertTrue(result.get(0) instanceof SessionStartedEvent, "Event type should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) result.get(0);
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should have been thrown");
        // We verify it's an IllegalStateException which is the standard Domain Error pattern in this codebase
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        
        // Validate the message matches one of our domain rules
        String msg = caughtException.getMessage();
        assertTrue(
            msg.contains("authenticated") || 
            msg.contains("timeout") || 
            msg.contains("Navigation state"),
            "Exception message should match a known domain rule violation. Got: " + msg
        );
    }
}
