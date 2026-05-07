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
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate an active session state
        aggregate.hydrate(
            "teller-1",
            TellerSessionAggregate.Status.ACTIVE,
            Instant.now().minusSeconds(60), // Last active 1 min ago
            "MAIN_MENU"
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate ID setup in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.hydrate(
            null, // No authenticated user
            TellerSessionAggregate.Status.ACTIVE,
            Instant.now(),
            "LOGIN"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.hydrate(
            "teller-1",
            TellerSessionAggregate.Status.ACTIVE,
            Instant.now().minus(Duration.ofMinutes(31)), // Inactive > 30 mins
            "MAIN_MENU"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.hydrate(
            "teller-1",
            TellerSessionAggregate.Status.ACTIVE,
            Instant.now(),
            null // Invalid navigation state
        );
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd("session-123");
        // Adjusting ID for specific test cases if needed, usually static is fine for unit test isolation
        if (aggregate.id().equals("session-401")) cmd = new EndSessionCmd("session-401");
        if (aggregate.id().equals("session-timeout")) cmd = new EndSessionCmd("session-timeout");
        if (aggregate.id().equals("session-nav-error")) cmd = new EndSessionCmd("session-nav-error");

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Verify it's the specific domain exception (IllegalStateException or custom)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
