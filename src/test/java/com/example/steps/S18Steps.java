package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Default valid data
    private final String validSessionId = "SESSION-123";
    private final String validTellerId = "TELLER-01";
    private final String validTerminalId = "TERM-042";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate(validSessionId);
        // Reset error state
        this.capturedException = null;
        this.resultEvents = null;
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in context setup, effectively a no-op but ensures scenario readability
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in context setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        executeCommand(true, "HOME", Instant.now().toEpochMilli());
    }

    private void executeCommand(boolean isAuthenticated, String navState, long lastActivity) {
        Command cmd = new StartSessionCmd(validTellerId, validTerminalId, isAuthenticated, navState, lastActivity);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(validTellerId, event.tellerId());
        Assertions.assertEquals(validTerminalId, event.terminalId());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(validSessionId);
        this.capturedException = null;
    }

    @When("the StartSessionCmd command is executed on unauthenticated session")
    public void the_StartSessionCmd_command_is_executed_unauthenticated() {
        executeCommand(false, "HOME", Instant.now().toEpochMilli());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(validSessionId);
        this.capturedException = null;
    }

    @When("the StartSessionCmd command is executed with stale activity")
    public void the_StartSessionCmd_command_is_executed_with_stale_activity() {
        // Set activity to 20 minutes ago (Configured timeout is 15 minutes)
        long staleActivity = Instant.now().minusSeconds(20 * 60).toEpochMilli();
        executeCommand(true, "HOME", staleActivity);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate(validSessionId);
        this.capturedException = null;
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_StartSessionCmd_command_is_executed_with_invalid_navigation_state() {
        // Use the specific error string defined in the aggregate
        executeCommand(true, "SYSTEM_ERROR", Instant.now().toEpochMilli());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "An exception should have been thrown");
        Assertions.assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
        Assertions.assertFalse(capturedException.getMessage().isBlank(), "Exception should have a message");
    }
}
