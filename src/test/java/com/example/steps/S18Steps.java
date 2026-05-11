package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String tellerId;
    private String terminalId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // By default make it valid for the happy path
        aggregate.markNavigationValid();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markUnauthenticated(); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        // Set last activity to 2 hours ago
        aggregate.setLastActivityTo(Instant.now().minus(Duration.ofHours(2)));
        // Set timeout to 30 mins (default)
        aggregate.setSessionTimeout(Duration.ofMinutes(30));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.markNavigationInvalid(); // Violation: Navigation state invalid
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "terminal-T1";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect an IllegalStateException based on our implementation
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("must") || thrownException.getMessage().contains("timeout"));
    }
}
