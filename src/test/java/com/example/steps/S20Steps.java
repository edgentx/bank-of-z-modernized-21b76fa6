package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private InMemoryTellerSessionRepository repo;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        repo = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup valid state
        aggregate.markOperationalContextValid();
        repo.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is implicitly handled by the aggregate initialization
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // persist state change
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
        assertFalse(aggregate.isActive());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        repo = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setUnauthenticated(); // Ensure not authenticated
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        repo = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); // Must be authenticated first to pass basic checks
        aggregate.setTimedOut(); // Set time in the past
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        repo = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated(); // Must be authenticated first
        aggregate.setInvalidContext(); // Set invalid context
        repo.save(aggregate);
    }
}
