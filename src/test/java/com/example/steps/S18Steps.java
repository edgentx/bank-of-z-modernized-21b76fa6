package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String currentTellerId = "teller-123";
    private String currentTerminalId = "terminal-A";
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-1";
        aggregate = repository.getOrCreate(sessionId);
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        a_valid_TellerSession_aggregate();
        // Simulate auth failure by setting an invalid ID for the execution context
        currentTellerId = null; // Or "" to trigger validation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        a_valid_TellerSession_aggregate();
        // Manually set the state to be expired to simulate the invariant check
        aggregate.markLastActivityAsExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        a_valid_TellerSession_aggregate();
        aggregate.markNavigationContextInvalid();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        currentTellerId = "teller-123";
    }

    @And("a valid terminalId is provided")
    public void a valid_terminalId_is_provided() {
        currentTerminalId = "terminal-456";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(currentTellerId, currentTerminalId);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("terminal-456", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain rules in the Aggregate throw IllegalArgumentException or IllegalStateException
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException or IllegalStateException), but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
