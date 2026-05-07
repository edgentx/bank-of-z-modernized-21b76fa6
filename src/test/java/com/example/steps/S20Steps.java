package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession aggregate;
    private InMemoryTellerSessionRepository repository;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSession("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setCurrentState("IDLE");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSession("session-auth-fail");
        aggregate.setAuthenticated(false); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSession("session-timeout");
        aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago (assuming timeout is 30)
        aggregate.setLastActivity(Instant.now().minusSeconds(31 * 60));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSession("session-nav-fail");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        // Use the specific state that triggers validation error in the aggregate
        aggregate.setCurrentState("INVALID_CONTEXT");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled by the aggregate initialization in the Given steps
        assertNotNull(aggregate.getSessionId());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Domain errors are typically IllegalStateExceptions or IllegalArgumentExceptions
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
