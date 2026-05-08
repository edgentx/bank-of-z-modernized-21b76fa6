package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private Exception capturedException;
    private static final String SESSION_ID = "sess-123";
    private static final String TELLER_ID = "teller-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup valid state: authenticated, valid navigation, recent activity
        aggregate.markActive(TELLER_ID, true, true, Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the aggregate initialization in the previous step
        // We verify the ID matches what we expect
        assertEquals(SESSION_ID, aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.tellerId());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup invalid state: NOT authenticated
        aggregate.markActive(TELLER_ID, false, true, Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup invalid state: Old timestamp (simulating timeout)
        // Assuming timeout is 15 mins, we go back 20 mins
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.markActive(TELLER_ID, true, true, oldTime);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup invalid state: Navigation invalid
        aggregate.markActive(TELLER_ID, true, false, Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
        // Ensure no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}
