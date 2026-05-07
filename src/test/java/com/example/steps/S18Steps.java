package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerauthentication.model.TellerAuthenticationAggregate;
import com.example.domain.tellerauthentication.model.AuthenticateTellerCmd;
import com.example.domain.tellerauthentication.model.TellerAuthenticatedEvent;
import com.example.domain.ui.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.Instant;
import java.util.UUID;

public class S18Steps {

    private TellerAuthenticationAggregate authAggregate;
    private TellerSessionAggregate sessionAggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup valid authentication context
        String authId = UUID.randomUUID().toString();
        authAggregate = new TellerAuthenticationAggregate(authId);
        authAggregate.execute(new AuthenticateTellerCmd(authId, "teller_123", "terminal_01"));
        
        // Assume the aggregate is hydrated from an event indicating authentication success
        String sessionId = UUID.randomUUID().toString();
        sessionAggregate = new TellerSessionAggregate(sessionId);
        sessionAggregate.apply(new TellerAuthenticatedEvent(sessionId, "teller_123", "terminal_01", Instant.now()));
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in session setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context handled in session setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionAggregate.id(), "teller_123", "terminal_01", "/HOME");
            resultEvents = sessionAggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        assertEquals("SessionStarted", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        String sessionId = UUID.randomUUID().toString();
        sessionAggregate = new TellerSessionAggregate(sessionId);
        // No authentication event applied, or explicitly set state to unauthenticated
        // (Default constructor state assumes not authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        sessionAggregate = new TellerSessionAggregate(sessionId);
        // Authenticated, but timed out
        sessionAggregate.apply(new TellerAuthenticatedEvent(sessionId, "teller_123", "terminal_01", Instant.now().minusSeconds(3600)));
        // Simulate violation (simulated via reflection or package-private setter for test, or assuming logic handles old timestamps)
        // For this pattern, we often rely on the Aggregate logic to check the timestamp.
        // Here we force the last activity time to be very old to trigger the invariant check.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        String sessionId = UUID.randomUUID().toString();
        sessionAggregate = new TellerSessionAggregate(sessionId);
        // Simulate an invalid state for the sake of the scenario (e.g. invalid current screen)
        // This is abstract, but we assume the aggregate knows it can't start from an invalid state.
        // Assuming valid auth but invalid navigation state (handled via abstract flag or setup)
        sessionAggregate.apply(new TellerAuthenticatedEvent(sessionId, "teller_123", "terminal_01", Instant.now()));
        // Manually corrupt state for test validity (In a real app, we might have a specific command to enter this state)
        // For BDD, we often rely on the execute logic to fail if the state isn't 'IDLE' or equivalent.
        // Let's assume the aggregate needs to be in a 'NAVIGABLE' state.
        // We will simulate this by not having the prerequisite events, or having a conflicting event.
        // For simplicity in this stub: we assume valid auth, but the execute() method enforces specific context rules.
        // To force failure, we might need the aggregate to believe it is already in a session that cannot be restarted.
        sessionAggregate.apply(new SessionStartedEvent(sessionId, "teller_123", "terminal_01", Instant.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted_duplicate() {
        // Duplicate to handle Gherkin flow if needed, or method reuse.
        a_session_started_event_is_emitted();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_duplicate() {
        the_command_is_rejected_with_a_domain_error();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_duplicate2() {
        the_command_is_rejected_with_a_domain_error();
    }
}