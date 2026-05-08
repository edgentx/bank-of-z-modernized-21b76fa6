package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.InvariantViolationException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "TELLER_101";
    private String validTerminalId = "TERM_01";
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION_123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // validTellerId is set
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // validTerminalId is set
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(validTellerId, validTerminalId, true, Instant.now().toEpochMilli(), "SIGN_ON");
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
        SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("SESSION_123", evt.aggregateId());
        assertEquals("session.started", evt.type());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("SESSION_FAIL_AUTH");
        // Simulate unauthenticated state via command flag in the When step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("SESSION_FAIL_TIMEOUT");
        // Simulate stale timestamp
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("SESSION_FAIL_NAV");
        // Simulate wrong state via command flag in the When step
    }

    @When("the StartSessionCmd command is executed with violation flags")
    public void the_StartSessionCmd_command_is_executed_with_violations() {
        try {
            // We dispatch based on which aggregate was created in the Given steps to set up specific violation conditions.
            // This is a simplification for the test structure.
            
            boolean auth = !aggregate.id().equals("SESSION_FAIL_AUTH");
            String navState = "SIGN_ON";
            long timestamp = Instant.now().toEpochMilli();

            if (aggregate.id().equals("SESSION_FAIL_AUTH")) {
                auth = false; // Violation
            } else if (aggregate.id().equals("SESSION_FAIL_NAV")) {
                navState = "TRANS_MAIN"; // Violation
            } else if (aggregate.id().equals("SESSION_FAIL_TIMEOUT")) {
                timestamp = Instant.now().minusSeconds(1000).toEpochMilli(); // Violation
            }

            StartSessionCmd cmd = new StartSessionCmd(validTellerId, validTerminalId, auth, timestamp, navState);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof InvariantViolationException);
    }

}
