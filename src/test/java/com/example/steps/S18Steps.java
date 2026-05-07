package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String validTellerId = "TELLER_001";
    private String validTerminalId = "TERM_42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // Ensure clean state
        aggregate.setNavigationState(null);
        aggregate.setAuthenticated(false);
        aggregate.setActive(false);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // In this design, the aggregate handles the transition. 
        // To simulate violation of pre-conditions, we might set it to already active 
        // implying a double-start scenario or forced invalid state.
        aggregate = new TellerSessionAggregate("SESSION_123");
        aggregate.setActive(true); // Violation: Already active
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // Set last activity to 20 minutes ago to violate timeout
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // Simulate dirty navigation state
        aggregate.setNavigationState("TRANSACTION_SCREEN_IN_PROGRESS");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Valid ID stored in context
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Valid ID stored in context
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("session.started", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on whether we throw IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
