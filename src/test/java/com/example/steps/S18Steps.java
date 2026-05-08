package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.SessionStartedEvent;
import com.example.domain.tellsession.model.StartSessionCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String providedTellerId;
    private String providedTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Simulate authenticated = false state implicitly or via constructor if needed
        // For this design, we assume the aggregate defaults to unauthenticated and we don't call a login command.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate timeout by creating the aggregate in a state where last activity was too long ago
        // Or just ensuring the command logic checks a config flag which we simulate via a test setup method
        // Since we don't have a 'Login' command yet, we might need a way to set the internal state to Active but Stale.
        // For the purpose of this test, we assume the aggregate logic checks a mockable/configurable time.
        // Implementation detail: we'll rely on the aggregate's internal clock check or a test-specific setup.
        // To keep it simple for S-18: We might assume the command itself carries the 'lastActivityTimestamp'
        // or we set a 'simulatedTime' on the aggregate.
        // Let's assume the command contains the timestamp of the request for this check.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Violation: e.g. terminal is already in use or context is invalid.
        // We will simulate this by setting a flag on the aggregate that the command checks.
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.providedTellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.providedTerminalId = "terminal-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), providedTellerId, providedTerminalId);
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
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors in DDD are often IllegalStateExceptions or IllegalArgumentExceptions
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Helper runners would be in a separate Suite file, but included here for context if needed
}