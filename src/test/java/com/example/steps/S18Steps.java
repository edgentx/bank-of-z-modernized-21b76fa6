package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup to fail auth check via the command later, but contextually we prepare the aggregate.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // We cannot easily manipulate time in the aggregate without a clock abstraction,
        // but the invariant check logic relies on 'lastActivityAt'.
        // Since we are testing the rejection, we construct the aggregate normally,
        // and the Step Implementation assumes the invariant logic would catch it.
        aggregate = new TellerSessionAggregate("session-123");
        // In a real test with a Clock, we'd set the clock back.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data setup happens in the 'When' step execution for simplicity
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Data setup happens in the 'When' step execution
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Determine context based on the previous Given step titles is hard in stateless steps.
        // We will interpret the violation scenarios based on specific data in the command.
        // However, since we can't pass state easily between steps without shared variables,
        // we will assume the standard happy path unless the exception is expected.
        // A more robust implementation would use scenario context or specific violation Given steps.
        
        // Interpretation:
        // Scenario 1: Happy path -> Valid Auth
        // Scenario 2: Auth Violation -> Invalid Auth
        // Scenario 3: Timeout -> (Simulated by aggregate state, but here we just run the command)
        // Scenario 4: Nav State -> Invalid Terminal ID

        String scenarioContext = getScenarioContextFromStackTrace(); // Hacky, but standard pure Cucumber doesn't pass context explicitly to steps.
        // Better approach: Use member variables to track 'currentViolationType' set in Given steps.

        String terminalId = "TERM-01";
        boolean isAuthenticated = true;

        if ("auth".equals(violationType)) {
            isAuthenticated = false;
        } else if ("nav".equals(violationType)) {
            terminalId = "INVALID";
        }
        // Timeout is handled internally in the aggregate logic for this test structure.

        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-01", terminalId, isAuthenticated);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    private String violationType = "none";

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setViolationAuth() { violationType = "auth"; }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setViolationNav() { violationType = "nav"; }
    
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setViolationTimeout() { violationType = "timeout"; }

    private String getScenarioContextFromStackTrace() {
        return "";
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Invariant checks throw IllegalStateException
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
