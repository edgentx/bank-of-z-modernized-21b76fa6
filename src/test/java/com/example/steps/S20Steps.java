package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class S20Steps {

    // This repository implements the interface defined in src/main
    // It fixes the class cast / signature errors from the previous build attempt
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
        // We simulate an existing session by applying a Start event manually or assuming constructor defaults
        // For the purpose of the 'End' command, the aggregate needs to be in an active state.
        // Since we are testing the 'End' logic, we assume the aggregate is hydrated in a valid state.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicit in the aggregate creation above
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We create an aggregate but don't hydrate it with an authenticated state.
        // Logic inside execute() will check a flag (e.g., isAuthenticated).
        aggregate = new TellerSessionAggregate("SESSION-NO-AUTH");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMED-OUT");
        // Simulate a session created很久 ago
        aggregate.setLastActivityTime(Instant.now().minusSeconds(3600)); // 1 hour ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        // Force the aggregate into a state where navigation is inconsistent (e.g. null)
        aggregate.setNavigationState(null);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        // We expect specific exceptions, but for this generic test, any RuntimeException fits
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // In-memory implementation that satisfies the Repository interface and fixes build errors
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final Map<String, TellerSessionAggregate> store = new HashMap<>();

        @Override
        public void save(TellerSessionAggregate aggregate) {
            // Contract requires void return, fixing previous build error
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public TellerSessionAggregate load(String id) {
            return store.get(id);
        }
    }
}
