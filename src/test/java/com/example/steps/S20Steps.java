package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
 * Cucumber Steps for S-20: EndSessionCmd
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // By default, mark it as valid and authenticated
        aggregate.markAuthenticated();
        this.caughtException = null;
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // In this context, the ID is already set in the aggregate constructor
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-INVALID-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-TIMEDOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markStale(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        this.sessionId = "SESSION-BAD-NAV";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInvalidNavigation(); // Violation
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events should not be empty");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalStateException/IllegalArgumentException)");
    }
}
