package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a valid session that is authenticated, active, and within timeout
        String sessionId = "sess-123";
        String tellerId = "teller-01";
        Instant now = Instant.now();
        
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate a prior StartSession event to hydrate the aggregate to a valid state
        // Note: In a real repository scenario, we would load and apply events. 
        // For unit testing the command logic, we assume the aggregate reflects a hydrated state.
        aggregate.hydrate(
            tellerId, 
            "main-menu", // valid context
            now.minus(Duration.ofMinutes(1)) // last active 1 min ago (within timeout)
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate ID in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "sess-401";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate a session state where the teller is NOT authenticated
        aggregate.hydrate(
            null, // Teller ID is null -> not authenticated
            "login-screen",
            Instant.now()
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-408";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate a session where last activity was 31 minutes ago (assuming 30 min timeout)
        aggregate.hydrate(
            "teller-01",
            "main-menu",
            Instant.now().minus(Duration.ofMinutes(31))
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-invalid-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate a corrupted state or a mismatch in context
        aggregate.hydrate(
            "teller-01",
            null, // Navigation state is null/corrupted
            Instant.now()
        );
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain error exception, but none was thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}