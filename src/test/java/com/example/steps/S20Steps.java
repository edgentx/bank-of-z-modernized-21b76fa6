package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Simulate a valid active session by applying a start event
        aggregate.applyPastEvents(List.of(
            new SessionStartedEvent(id, "teller_001", Instant.now())
        ));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID already set in aggregate creation
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Session exists but is not authenticated (no started event applied)
        // or we manually set internal state if needed, but the aggregate checks for 'active' status.
        // In this model, if it's not started, it's not authenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Create a session that started a long time ago (exceeding timeout)
        Instant oldTime = Instant.now().minus(Duration.ofHours(2));
        aggregate.applyPastEvents(List.of(
            new SessionStartedEvent(id, "teller_001", oldTime)
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.applyPastEvents(List.of(
            new SessionStartedEvent(id, "teller_001", Instant.now())
        ));
        // Simulate a state that is inconsistent (e.g., locked)
        aggregate.lockForMaintenance();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on the specific error type (IllegalStateException, IllegalArgumentException, etc.)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}