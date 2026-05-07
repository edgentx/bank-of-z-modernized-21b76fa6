package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = "SESSION-1";
        aggregate = new TellerSession(id);
        // Initialize valid state via direct event application or constructor defaults
        // Simulating a started session
        aggregate.apply(new SessionStartedEvent(id, "TELLER-1", Instant.now()));
        // Reset version to simulate hydrated state
        aggregate.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // ID is set in aggregate creation
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("SESSION-AUTH-FAIL");
        // Aggregate is created but never authenticated/started
        // No SessionStartedEvent applied, so isAuthenticated is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("SESSION-TIMEOUT");
        aggregate.apply(new SessionStartedEvent("SESSION-TIMEOUT", "TELLER-1", Instant.now().minus(Duration.ofHours(2))));
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("SESSION-NAV-FAIL");
        aggregate.apply(new SessionStartedEvent("SESSION-NAV-FAIL", "TELLER-1", Instant.now()));
        // Simulate an active transaction lock that prevents ending
        aggregate.setTransactionLocked(true);
        aggregate.clearEvents();
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
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Domain logic exceptions are typically IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
