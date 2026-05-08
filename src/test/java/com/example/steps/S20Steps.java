package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Scenario: Successfully execute EndSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize to a valid state for the command to succeed
        aggregate.initialize("teller-1", "MAIN_MENU", Instant.now());
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the setup in 'a_valid_teller_session_aggregate'
        assertNotNull(repository.findById("session-123").orElse(null));
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd("session-123");
            resultEvents = aggregate.execute(cmd);
            // Apply result to in-memory aggregate if necessary, though tests often just inspect returned list
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("session.ended", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Scenario: EndSessionCmd rejected (Auth) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Initialize with unauthenticated state
        aggregate.initialize("teller-1", "MAIN_MENU", Instant.now());
        aggregate.markUnauthenticated(); // Force violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(caughtException.getMessage().contains("Cannot end session") || 
                   caughtException.getMessage().contains("authenticated"));
    }

    // --- Scenario: EndSessionCmd rejected (Timeout) ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.initialize("teller-1", "MAIN_MENU", Instant.now());
        aggregate.markStale(); // Force violation (set activity to past)
    }

    // --- Scenario: EndSessionCmd rejected (Navigation State) ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.initialize("teller-1", "MAIN_MENU", Instant.now());
        aggregate.markNavigationInvalid(); // Force violation
    }

}
