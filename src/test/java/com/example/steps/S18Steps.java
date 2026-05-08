package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: Teller Session Start.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-05";
    private boolean isAuthenticated = true;
    private int timeoutInSeconds = 900;
    private String navigationContext = "MAIN_MENU";
    
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.timeoutInSeconds = 900;
        this.navigationContext = "MAIN_MENU";
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Setup handled in constructor/given defaults
        this.tellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Setup handled in constructor/given defaults
        this.terminalId = "term-05";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            tellerId,
            terminalId,
            isAuthenticated,
            timeoutInSeconds,
            navigationContext
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(sessionId, startedEvent.aggregateId());
        assertNotNull(startedEvent.occurredAt());
        assertEquals(tellerId, startedEvent.tellerId());
        assertEquals(terminalId, startedEvent.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = false; // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.timeoutInSeconds = -10; // Violation: Invalid timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.timeoutInSeconds = 900;
        this.navigationContext = ""; // Violation: Blank context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
