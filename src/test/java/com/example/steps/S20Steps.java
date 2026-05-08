package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.navigation.repository.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;

public class S20Steps {
    
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("SESSION-1");
        // Simulate the aggregate being in a valid, started state by applying a start event directly
        // or by ensuring the constructor sets it to a state where EndSessionCmd is valid.
        // For BDD purposes, we treat a fresh instance as 'valid' context until violated.
        // However, to ensure 'active' status for success, we'll assume the context implies an active session.
        // We use reflection or a package-private helper to set the state to ACTIVE for the 'Success' scenario
        // or we execute a Start command. Here we just set the internal state via reflection or similar if needed.
        // Let's assume the aggregate starts as ACTIVE for the 'valid' scenario.
        try {
            var field = TellerSession.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(aggregate, TellerSession.Status.ACTIVE);
            field = TellerSession.class.getDeclaredField("lastActivityAt");
            field.setAccessible(true);
            field.set(aggregate, Instant.now());
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup aggregate state", e);
        }
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is implicitly "SESSION-1" from the setup above.
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("SESSION-AUTH-FAIL");
        // Set status to AUTH_FAILED or similar to simulate the violation condition
        try {
            var field = TellerSession.class.getDeclaredField("authenticated");
            field.setAccessible(true);
            field.set(aggregate, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("SESSION-TIMEOUT");
        try {
            var field = TellerSession.class.getDeclaredField("lastActivityAt");
            field.setAccessible(true);
            field.set(aggregate, Instant.now().minusSeconds(3600)); // 1 hour ago
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("SESSION-NAV-FAIL");
        try {
            var field = TellerSession.class.getDeclaredField("navigationState");
            field.setAccessible(true);
            field.set(aggregate, "INVALID_STATE_MISMATCH");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), Instant.now());
            List<DomainEvent> events = aggregate.execute(cmd);
            if (events.isEmpty()) {
                throw new IllegalStateException("Expected an event, but none was emitted.");
            }
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        List<DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected at least one event");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
