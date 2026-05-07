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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // --- Scenario: Successfully execute EndSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        this.aggregate.markAuthenticated(true);
        this.aggregate.setNavigationState("MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(this.sessionId);
    }

    // --- Scenarios: Rejected Commands ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "TS-401";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // State: NOT authenticated
        this.aggregate.markAuthenticated(false);
        this.aggregate.setNavigationState("LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "TS-402";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // State: Authenticated, but inactive for too long
        this.aggregate.markAuthenticated(true);
        this.aggregate.setNavigationState("IDLE");
        // Set last activity to 16 minutes ago (Configured timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(16)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-403";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // State: Authenticated, but navigation state is null/invalid
        this.aggregate.markAuthenticated(true);
        this.aggregate.setNavigationState(null); // Invalid state
    }

    // --- Execution ---

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(this.sessionId);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify the message relates to the context
        assertTrue(thrownException.getMessage().contains("Cannot end session"));
    }
}
