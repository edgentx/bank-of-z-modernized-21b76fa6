package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "sess-valid";
        aggregate = new TellerAggregate(sessionId);
        // Simulate session started state
        aggregate.apply(new SessionStartedEvent(sessionId, "teller123", Instant.now(), Duration.ofHours(8)));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // An aggregate that exists but has no authenticated teller (not started)
        String sessionId = "sess-no-auth";
        aggregate = new TellerAggregate(sessionId);
        // Do not apply SessionStartedEvent, leaving state unauthenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerAggregate(sessionId);
        // Create a session that started 10 hours ago (assuming 8h timeout)
        Instant past = Instant.now().minus(Duration.ofHours(10));
        aggregate.apply(new SessionStartedEvent(sessionId, "teller123", past, Duration.ofHours(8)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        String sessionId = "sess-bad-nav";
        aggregate = new TellerAggregate(sessionId);
        // Create valid session
        aggregate.apply(new SessionStartedEvent(sessionId, "teller123", Instant.now(), Duration.ofHours(8)));
        // Manually corrupt state or simulate a locked context that prevents ending
        // For this aggregate, we assume 'locked' state prevents ending
        aggregate.lockNavigation(); // Hypothetical method to set state
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
            // Ensure the repo reflects the change if needed for verification, though events are primary
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertEquals("session.ended", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no error, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In Domain-Driven Design, invariant violations are often IllegalState or IllegalArgument
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
        assertNull(resultEvents);
    }
}