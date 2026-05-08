package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "TS-12345";
        aggregate = new TellerSessionAggregate(id);
        // Simulate a previously started session to allow ending
        aggregate.apply(new SessionStartedEvent(id, "Teller-01", "MAIN_MENU", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        // A session that exists but has no authenticated teller context (e.g. zombie state)
        String id = "TS-UNAUTH";
        aggregate = new TellerSessionAggregate(id);
        // Note: Aggregate starts empty. Attempting to end without a start (auth) is the violation.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(id);
        // Simulate a session that started way in the past (> 15 minutes ago)
        Instant ancientTime = Instant.now().minus(Duration.ofHours(1));
        aggregate.apply(new SessionStartedEvent(id, "Teller-02", "MAIN_MENU", ancientTime));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        String id = "TS-NAV-ERR";
        aggregate = new TellerSessionAggregate(id);
        aggregate.apply(new SessionStartedEvent(id, "Teller-03", "MAIN_MENU", Instant.now()));
        // Corrupt state via reflection or restricted accessor to simulate inconsistent context
        // For this test, we assume the aggregate has logic to detect desynchronization.
        // We mark it as desynchronized.
        aggregate.markDesynchronized();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            List<DomainEvent> events = aggregate.execute(cmd);
            aggregate.apply(events.get(0)); // Apply the resulting event to update state
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(aggregate);
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionEndedEvent);
        assertTrue(((SessionEndedEvent) aggregate.uncommittedEvents().get(0)).occurredAt() != null);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException or IllegalArgumentException based on implementation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}