package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Simple in-memory repo simulation could go here, but direct aggregate usage is sufficient for unit logic testing.

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "SESSION-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        aggregate.markAuthenticated("TELLER-123");
        aggregate.setNavigationState("IDLE");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally not calling markAuthenticated. isAuthenticated remains false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-123");
        aggregate.markTimedOut(); // Adjust internal clock to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "SESSION-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-123");
        // Set a state that is not IDLE, implying an active operation context
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId already initialized in @Given blocks
        Assertions.assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            this.resultEvents = aggregate.execute(cmd);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We expect IllegalStateException for domain invariant violations
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
