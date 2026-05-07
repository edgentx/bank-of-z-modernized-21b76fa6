package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.exceptions.DomainException;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize state to valid/active
        aggregate.initializeState("TELLER-1", Instant.now(), "MAIN_MENU");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Unauthenticated state
        aggregate = new TellerSessionAggregate("TS-401");
        aggregate.initializeState(null, Instant.now(), "LOGIN_SCREEN");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-408");
        // Set last activity to 2 hours ago (configured timeout is usually 30 mins)
        Instant past = Instant.now().minus(Duration.ofHours(2));
        aggregate.initializeState("TELLER-1", past, "MAIN_MENU");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("TS-NAV-ERR");
        // Inconsistent state: Active but logically in an invalid screen for EndSession
        // e.g. stuck in a modal or transaction processing
        aggregate.initializeState("TELLER-1", Instant.now(), "PROCESSING_TRANSACTION_BLOCK");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // implicitly handled by the setup of the aggregate in the Given steps
        assertNotNull(aggregate);
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof DomainException, 
                   "Expected domain logic exception");
    }
}