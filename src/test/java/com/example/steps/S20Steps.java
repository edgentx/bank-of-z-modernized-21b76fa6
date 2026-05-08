package com.example.steps;

import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Setup a valid, authenticated, active session
        String sessionId = "SESSION-123";
        String tellerId = "TELLER-42";
        Instant lastActivity = Instant.now();
        String currentState = "MAIN_MENU"; // Valid state

        aggregate = new TellerSessionAggregate(sessionId);
        // We use reflection or a package-private setup helper to initialize state for testing purposes
        // simulating that the session was previously started.
        aggregate.__internalForTestOnly_setState(tellerId, lastActivity, currentState, false);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate initialization in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "SESSION-UNAUTH";
        // Null tellerId implies unauthenticated in our domain logic
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internalForTestOnly_setState(null, Instant.now(), "MAIN_MENU", false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-TIMEOUT";
        Instant oldTime = Instant.now().minus(Duration.ofHours(2)); // Expired
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internalForTestOnly_setState("TELLER-1", oldTime, "MAIN_MENU", false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "SESSION-BAD-STATE";
        // "UNKNOWN" is not a valid navigation state in this context
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.__internalForTestOnly_setState("TELLER-1", Instant.now(), "UNKNOWN", false);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("session.ended", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We check for specific exception types based on the scenario context
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException);
    }
}
