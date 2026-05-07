package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "SESSION-123";
        // Initialize a valid aggregate. In a real scenario, this might be loaded from a repo.
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Manually setting state to valid for the test context, simulating a prior 'StartSession'
        // Since we don't have the StartSessionCmd implementation, we assume the aggregate is constructed in a valid state for the success case,
        // or we allow the default constructor to represent a fresh, valid state context.
        // For the purpose of testing EndSession, we assume valid preconditions if not specified otherwise.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the previous step
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-401";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state to unauthenticated to trigger the invariant violation
        aggregate.setAuthenticated(false); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-408";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state to timed out to trigger the invariant violation
        aggregate.setTimedOut(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "SESSION-400";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state to invalid navigation context
        aggregate.setNavigationStateInvalid(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // In the aggregate, we throw IllegalStateException or IllegalArgumentException for domain errors
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}