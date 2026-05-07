package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.commands.EndSessionCmd;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-001"); // Setup valid state
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the aggregate construction
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            var cmd = new EndSessionCmd("session-123");
            resultEvents = aggregate.execute(cmd);
            // Save changes if successful
            if (!resultEvents.isEmpty()) {
                repository.save(aggregate);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("session.ended", event.type());
        assertTrue(aggregate.isEnded());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-no-auth");
        // Intentionally do NOT call markAuthenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated("teller-001");
        aggregate.setOperationalContext("IN_FLIGHT"); // Invalid state for ending
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

}