package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        aggregate.hydrate(new TellerSessionAggregate.TellerSessionState(
            "TELLER-101", 
            true, 
            Instant.now().minusSeconds(60), 
            TellerSessionAggregate.NavigationState.MAIN_MENU
        ));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicit in the aggregate construction in this test context
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("SESSION-1", event.aggregateId());
        assertEquals("session.ended", event.type());
    }

    // Scenario 2: Auth Violation
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        // Hydrate with authenticated = false
        aggregate.hydrate(new TellerSessionAggregate.TellerSessionState(
            null, 
            false, 
            Instant.now(), 
            TellerSessionAggregate.NavigationState.MAIN_MENU
        ));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }

    // Scenario 3: Timeout Violation
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        // Hydrate with a last activity time way in the past (e.g., 30 minutes ago)
        aggregate.hydrate(new TellerSessionAggregate.TellerSessionState(
            "TELLER-101", 
            true, 
            Instant.now().minus(Duration.ofMinutes(30)), 
            TellerSessionAggregate.NavigationState.MAIN_MENU
        ));
    }

    // Scenario 4: Navigation State Violation
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // Hydrate with an UNKNOWN or invalid navigation state
        aggregate.hydrate(new TellerSessionAggregate.TellerSessionState(
            "TELLER-101", 
            true, 
            Instant.now(), 
            TellerSessionAggregate.NavigationState.UNKNOWN
        ));
    }
}
