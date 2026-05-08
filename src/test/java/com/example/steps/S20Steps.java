package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String currentSessionId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        currentSessionId = "session-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Ensure it is in a valid default state (authenticated, active)
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setContextValid(true);
        
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is already set in the previous step
        assertNotNull(currentSessionId);
    }

    // --- Negative / Invariant Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        currentSessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.setAuthenticated(false); // Violate invariant
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_is_timed_out() {
        currentSessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Set activity to 2 hours ago to violate timeout (assuming 30 min timeout)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(7200));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_with_invalid_context() {
        currentSessionId = "session-bad-context";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.setContextValid(false); // Violate invariant
        repository.save(aggregate);
    }

    // --- Action ---

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            // Reload from repo to ensure we are working with persisted state simulation
            aggregate = repository.findById(currentSessionId).orElseThrow();
            
            EndSessionCmd cmd = new EndSessionCmd(currentSessionId);
            resultEvents = aggregate.execute(cmd);
            
            // Persist result
            repository.save(aggregate);
        } catch (IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals(currentSessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error (Exception)");
        // Verify it's a logic/state error, not a NPE or System error
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof UnknownCommandException ||
            caughtException instanceof IllegalArgumentException,
            "Expected a domain logic exception, got: " + caughtException.getClass().getSimpleName()
        );
    }
}
