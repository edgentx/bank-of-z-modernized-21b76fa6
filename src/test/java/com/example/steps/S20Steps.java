package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.SessionEndedEvent;
import com.example.domain.navigation.model.TellerSession;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSession session;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        session = new TellerSession(sessionId);
        // Initialize to a valid state for a teller named "teller-1"
        session.init("teller-1", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled by the aggregate instance in this context
        // This step is a no-op but ensures flow correctness in Gherkin
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        session = new TellerSession(sessionId);
        session.init("teller-1", Instant.now());
        // Violate: De-authenticate the teller
        session.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_has_timed_out() {
        String sessionId = "session-timeout";
        session = new TellerSession(sessionId);
        session.init("teller-1", Instant.now().minus(Duration.ofHours(1)));
        // Set last activity to 31 minutes ago to exceed the default 30 minute timeout
        session.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "session-inactive";
        session = new TellerSession(sessionId);
        session.init("teller-1", Instant.now());
        // Violate: Mark session as inactive (terminated/closed)
        session.setActive(false);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            // We assume the command is for the same teller who initialized the session
            // If the session is "teller-1", we use that ID.
            String currentTellerId = "teller-1";
            EndSessionCmd cmd = new EndSessionCmd(session.id(), currentTellerId);
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        assertEquals("session.ended", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
