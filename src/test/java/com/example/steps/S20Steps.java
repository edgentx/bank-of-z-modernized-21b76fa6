package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.EndSessionCmd;
import com.example.domain.uimodel.model.SessionEndedEvent;
import com.example.domain.uimodel.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it is authenticated for the happy path
        aggregate.markAuthenticated();
        aggregate.clearEvents(); // Clear creation events if any
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is already set in the previous step
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultingEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    // --- Scenarios for Rejections ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark as authenticated. The aggregate defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago to simulate timeout (default is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        sessionId = "session-nav-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Force invalid state
        aggregate.invalidateNavigationState();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
