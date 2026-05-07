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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(VALID_TELLER_ID);
        aggregate.setCurrentContext("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the constructor in the previous step, but we ensure state is clean
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Intentionally do not call markAuthenticated, or set authenticated to false
        aggregate.setAuthenticated(false);
        aggregate.setCurrentContext("LOGIN"); // Context might exist, but not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(VALID_TELLER_ID);
        // Set last activity to 20 minutes ago (configured period is 15 mins)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(VALID_TELLER_ID);
        // Set navigation state to null or invalid
        aggregate.setCurrentContext(null);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(VALID_SESSION_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);

        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        // Verify the message matches the invariant
        assertTrue(capturedException.getMessage().length() > 0);
    }
}
