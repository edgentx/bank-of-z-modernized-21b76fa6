package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.uimodel.repository.TellerSessionRepository as UiRepo; // Alias for clarity if needed, though distinct packages usually suffice.
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // In-memory repository simulation logic is implicit in the test setup

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Parameter is stored in the context for the command creation
        this.tellerId = "TELLER-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "TERM-A01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("SESSION-123", event.aggregateId());
        Assertions.assertEquals("session.started", event.type());
    }

    // Scenarios for Rejections

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Simulating a state where the teller is not authenticated
        // The aggregate might throw an error if the command doesn't contain valid auth tokens
        // For this test, we can just execute the command without the necessary valid state setup
        // or assuming the command itself triggers the check.
        this.aggregate = new TellerSessionAggregate("SESSION-FAIL-AUTH");
        this.tellerId = "UNAUTHENTICATED_USER";
        this.terminalId = "TERM-A01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // In a real scenario, this might involve replaying events that put the session in a stale state
        // Here we simulate the aggregate rejecting because the context is invalid
        this.aggregate = new TellerSessionAggregate("SESSION-FAIL-TIMEOUT");
        this.tellerId = "TELLER-001";
        this.terminalId = "TERM-A01";
        // Note: Actual timeout logic depends on the aggregate's internal clock/state checks
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("SESSION-FAIL-NAV");
        this.tellerId = "TELLER-001";
        this.terminalId = "TERM-A01";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Checking for IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Fields to hold scenario state
    private String tellerId;
    private String terminalId;
}
