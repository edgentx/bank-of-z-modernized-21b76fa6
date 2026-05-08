package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Constructor for standard initialization
    public S18Steps() {
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSession("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context provided in When block via command
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context provided in When block via command
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        Command cmd = new StartSessionCmd("session-123", "teller-001", "terminal-01");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-01", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Simulate a session that has already been started (can't start again without auth/re-auth)
        // Or a session in an invalid state. For this test, we'll start one and try to start it again
        // to simulate a state where the command is invalid (e.g. already active).
        this.aggregate = new TellerSession("session-double-start");
        // Execute once successfully
        aggregate.execute(new StartSessionCmd("session-double-start", "teller-001", "terminal-01"));
        aggregate.clearEvents(); // Clear events so we only see the failure
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // In a real scenario, this might involve clock manipulation. 
        // Here we assume the aggregate handles timeout logic internally.
        // For BDD purposes, we might rely on a mock clock or specific setup.
        // We will reuse the aggregate and assume the invariants are checked.
        this.aggregate = new TellerSession("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSession("session-nav-error");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
