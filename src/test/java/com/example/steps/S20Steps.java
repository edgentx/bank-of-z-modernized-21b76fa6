package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession session;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        session = new TellerSession(sessionId);
        // Simulate a started session with authenticated state
        session.start("teller123", Instant.now());
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        session = new TellerSession(sessionId);
        // Session exists but was never properly started/authenticated
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        session = new TellerSession(sessionId);
        session.start("teller123", Instant.now().minus(Duration.ofHours(2)));
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        session = new TellerSession(sessionId);
        session.start("teller123", Instant.now());
        // Force session into an inconsistent state (e.g., locked or pending action)
        session.forceNavigationState("LOCKED_ERROR");
        repository.save(session);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(session);
        assertNotNull(session.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id());
            List<DomainEvent> events = session.execute(cmd);
            // Apply events to update aggregate state in memory for verification
            events.forEach(e -> repository.save(session)); 
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        List<DomainEvent> uncommitted = session.uncommittedEvents();
        assertFalse(uncommitted.isEmpty(), "Expected events to be emitted");
        assertTrue(uncommitted.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) uncommitted.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(session.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We expect IllegalStateException or IllegalArgumentException for domain violations
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
