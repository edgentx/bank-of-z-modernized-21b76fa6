package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Initialize to a valid state (authenticated, active, valid nav)
        aggregate.initializeExistingSession(
            "teller-1", 
            true, 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            "DASHBOARD"
        );
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already part of the aggregate setup in 'a_valid_TellerSession_aggregate'
        // This step ensures context clarity.
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            // Reload from repository to simulate clean fetch
            TellerSessionAggregate session = repository.findById(aggregate.id());
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = session.execute(cmd);
            // Save the updated aggregate state
            repository.save(session);
            this.aggregate = session; // Update reference for assertions
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "sess-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Not authenticated
        aggregate.initializeExistingSession(
            "teller-unknown", 
            false, 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            "LOGIN"
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Timeout set in the past
        aggregate.initializeExistingSession(
            "teller-1", 
            true, 
            Instant.now().minusSeconds(400), 
            Instant.now().minusSeconds(100), 
            "DASHBOARD"
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-nav-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Invalid/Blank Nav State
        aggregate.initializeExistingSession(
            "teller-1", 
            true, 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            "" // Blank
        );
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}