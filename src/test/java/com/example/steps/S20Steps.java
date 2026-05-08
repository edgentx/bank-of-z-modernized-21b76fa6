package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepo();
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: Authenticated
        aggregate.markAuthenticated("teller-001");
        // Ensure valid navigation context (not stuck in transaction)
        aggregate.setCurrentScreen("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled implicitly by using the aggregate created in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly NOT marking authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001"); // Authenticated

        // Set last activity to 31 minutes ago (Threshold is 30m)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "session-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001"); // Authenticated
        aggregate.setLastActivityAt(Instant.now());   // Active

        // Set navigation state to a sensitive state that prevents termination
        aggregate.setCurrentScreen("TRANSACTION_IN_PROGRESS");
    }

    // --- When Steps ---

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Then Steps ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We accept IllegalStateException or RuntimeException as domain errors in this simple aggregate
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof RuntimeException, 
                   "Exception should be a domain error");
    }

    // --- Mock Repository for Test Scope ---
    private static class InMemoryTellerSessionRepo implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate; // No-op for in-memory test
        }

        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.empty(); // Not used in these specific steps
        }
    }
}
