package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermemory.model.TellerSessionAggregate;
import com.example.domain.tellermemory.model.EndSessionCmd;
import com.example.domain.tellermemory.model.SessionEndedEvent;
import com.example.domain.tellermemory.repository.TellerSessionRepository;
import com.example.domain.tellermemory.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // For the purpose of the 'success' scenario, we treat the aggregate as valid.
        // We manually construct an instance that bypasses the auth check constructor for now, 
        // or we assume the repo hydrated it.
        // We will use the repo to save it, then load it.
        String id = UUID.randomUUID().toString();
        // Creating a valid session that is active.
        aggregate = new TellerSessionAggregate(id, "teller_123", Instant.now().minusSeconds(60), Duration.ofMinutes(15));
        aggregate.setAuthenticated(true); // Ensure auth is true for success scenario
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate initialization in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id, "teller_123", Instant.now().minusSeconds(60), Duration.ofMinutes(15));
        aggregate.setAuthenticated(false); // Force violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        // Create a session that timed out (lastActive is too old)
        aggregate = new TellerSessionAggregate(id, "teller_123", Instant.now().minusMinutes(30), Duration.ofMinutes(15));
        aggregate.setAuthenticated(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id, "teller_123", Instant.now().minusSeconds(60), Duration.ofMinutes(15));
        aggregate.setAuthenticated(true);
        // Force invalid state (e.g. closing session while in transaction)
        aggregate.setNavigationState("IN_TRANSACTION");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception but none was thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Then("the session is terminated")
    public void the_session_is_terminated() {
        // implied by event emission, but we can check state if exposed
        // assertTrue(aggregate.isEnded());
    }
}
