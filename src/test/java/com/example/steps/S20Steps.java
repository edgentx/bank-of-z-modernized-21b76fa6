package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "SESSION-12345";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a logged-in, active session
        this.aggregate.markAsAuthenticated("TELLER-01");
        // Default to a safe navigation state
        this.aggregate.setCurrentLocation("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is already set in the previous step
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly force an inactive state
        this.aggregate.forceInactiveState();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Authenticate first
        this.aggregate.markAsAuthenticated("TELLER-02");
        // Force the timestamp back
        this.aggregate.forceTimeoutState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "SESSION-BUSY";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAsAuthenticated("TELLER-03");
        // Set a location that is considered "critical" by the aggregate logic
        this.aggregate.setCurrentLocation("CASH_WITHDRAWAL");
    }

    // --- Whens ---

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    // --- Thens ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertFalse(resultEvents.isEmpty(), "Expected list of events to not be empty");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Expected SessionEndedEvent, got " + event.getClass().getSimpleName());
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(sessionId, endedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown, but command succeeded");
        
        // Verify it's an IllegalStateException (standard Java domain error)
        assertTrue(capturedException instanceof IllegalStateException, 
            "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName());
            
        // Optional: Verify the message content based on the scenario
        System.out.println("Domain error captured: " + capturedException.getMessage());
    }
}
