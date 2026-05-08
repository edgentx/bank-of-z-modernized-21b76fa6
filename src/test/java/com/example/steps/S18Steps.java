package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private DomainEvent resultingEvent;
    private Exception thrownException;

    // Scenario State
    private boolean stubIsAuthenticated = true;
    private boolean stubIsTerminalAvailable = true;
    private boolean stubIsWithinTimeout = true;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        stubIsAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Note: The command simulation reflects the state. 
        // For this test to pass rejection, we simulate that the check fails.
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        stubIsWithinTimeout = false;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        stubIsTerminalAvailable = false;
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // No-op, handled in the When step
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // No-op, handled in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                "teller-001", 
                "term-42", 
                stubIsAuthenticated, 
                stubIsTerminalAvailable, 
                stubIsWithinTimeout
            );
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvent, "Expected an event to be emitted");
        assertTrue(resultingEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        SessionStartedEvent evt = (SessionStartedEvent) resultingEvent;
        assertEquals("session.started", evt.type());
        assertEquals("teller-001", evt.tellerId());
        assertEquals("term-42", evt.terminalId());
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected a domain error exception");
        // Verify it is an unchecked exception (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected IllegalStateException or IllegalArgumentException, but got: " + thrownException.getClass().getSimpleName());
        assertNull(resultingEvent, "Expected no event to be emitted on error");
    }
}
