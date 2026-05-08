package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated and active
        aggregate.markAuthenticated();
        aggregate.markInTransaction(false); // Not in transaction
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Id is already set in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure NOT authenticated
        // (default is false, but explicitly ensuring if we add constructors later)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-TIMEDOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set activity to > 30 minutes ago
        Instant past = Instant.now().minusSeconds(1801); // 30m + 1s
        aggregate.setLastActivityAt(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        this.sessionId = "SESSION-BUSY";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Simulate being "in transaction" or deep in a menu where logout is invalid
        aggregate.markInTransaction(true);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("session.ended", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
