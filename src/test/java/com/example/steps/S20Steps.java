package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.command.EndSessionCmd;
import com.example.domain.tellersession.event.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup a valid state (Authenticated, Active, Valid Nav)
        aggregate.authenticate("TELLER-001");
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling authenticate(). Active is false by default.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-001"); // Valid auth
        aggregate.setNavigationState("IDLE");  // Valid nav
        // Set last activity to 2 hours ago to violate timeout
        aggregate.setLastActivityAt(Instant.now().minusSeconds(7200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-001");
        // Set navigation to null or blank to simulate invalid/inconsistent state
        aggregate.setNavigationState("");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate construction in the Given steps
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultingEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultingEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultingEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Check specific error messages if necessary, or just presence of exception
    }
}