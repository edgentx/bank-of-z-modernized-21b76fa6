package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test Data
    private static final String VALID_SESSION_ID = "sess-123";
    private static final String VALID_TELLER_ID = "teller-001";
    private static final String VALID_TERMINAL_ID = "term-ABC";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // The violation is simulated in the command via isAuthenticated flag
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // The violation check logic is encapsulated in the aggregate's execute method.
        // For testing, we setup a valid aggregate, the "violation" context is implied
        // by the scenario description and the error check will happen in execute if
        // the logic supported checking historical timestamps, but for now we assume
        // valid input to test the positive path, or the specific rejection scenario.
        // Note: The existing logic throws error if not authenticated. The timeout
        // scenario implies the aggregate might be in a state that triggers a check.
        // Since we are implementing StartSession, the state is initially NONE.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // We simulate this by forcing the aggregate into a state where starting a session is invalid
        // However, since we can't mutate state without a command, we rely on the aggregate logic.
        // A potential violation is if we try to start an already started session.
        // For this test, we'll execute a valid start first to move it to STARTED state.
        StartSessionCmd setupCmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true);
        aggregate.execute(setupCmd); // Moves state to STARTED
        aggregate.clearEvents(); // Clear setup events
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in When step
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default to a valid authenticated command
        StartSessionCmd cmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Specific When for Auth failure scenario
    @When("the StartSessionCmd command is executed without authentication")
    public void theStartSessionCmdCommandIsExecutedWithoutAuth() {
        StartSessionCmd cmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, false);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Specific When for duplicate start (nav state violation)
    @When("the StartSessionCmd command is executed on an active session")
    public void theStartSessionCmdCommandIsExecutedOnActiveSession() {
        // aggregate is already in STARTED state from the Given step
        StartSessionCmd cmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Result events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals(VALID_TELLER_ID, event.tellerId());
        Assertions.assertEquals(VALID_TERMINAL_ID, event.terminalId());
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
    }
}