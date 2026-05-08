package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        aggregate.markAuthenticated(true);
        aggregate.markNavigationValid(true);
        aggregate.markTimedOut(false);
        aggregate.activate();
        
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the previous step setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-violate-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(false); // Violation
        aggregate.markNavigationValid(true);
        aggregate.markTimedOut(false);
        aggregate.activate();
        
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-violate-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.markNavigationValid(true);
        aggregate.markTimedOut(true); // Violation
        aggregate.activate();
        
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        String sessionId = "session-violate-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.markNavigationValid(false); // Violation
        aggregate.markTimedOut(false);
        aggregate.activate();
        
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            // Reload from repo to ensure we are testing the persistence flow (even if in-memory)
            TellerSessionAggregate agg = repository.findById(aggregate.id()).orElseThrow();
            EndSessionCmd cmd = new EndSessionCmd(agg.id(), Instant.now());
            this.resultEvents = agg.execute(cmd);
            // Save the mutated aggregate
            repository.save(agg);
            this.aggregate = agg;
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // We expect IllegalStateException for invariant violations based on the Aggregate implementation
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
