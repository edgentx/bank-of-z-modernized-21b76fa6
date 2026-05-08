package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in 'When' block for simplicity in this context
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in 'When' block
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-violate-auth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-violate-timeout");
        // We can't easily mock time passing without a Clock, but we can simulate the domain error logic
        // by assuming the command itself carries the timestamp/state that triggers validation.
        // For this test, we'll rely on the command to trigger the logic or assume state is pre-loaded.
        // Given the simple aggregate, we rely on the execution logic.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-violate-nav");
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid values, modified by specific violation scenarios if we had parameterization
            // For simplicity, we construct the command here.
            boolean isAuthenticated = true;
            String navState = "IDLE";

            // Apply scenario specific overrides based on aggregate ID (simple heuristic for demo)
            if (aggregate.id().contains("auth")) isAuthenticated = false;
            if (aggregate.id().contains("nav")) navState = "TRANSITIONAL_INVALID";

            this.command = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", isAuthenticated, navState);
            this.resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            this.capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Expected success but got exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected domain error but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
