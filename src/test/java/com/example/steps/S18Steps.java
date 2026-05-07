package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<SessionStartedEvent> resultEvents;
    private Exception caughtException;

    // Standard valid test data
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_3270_05";
    private static final String VALID_NAV_STATE = "HOME_SCREEN";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION_01");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context construction for 'When'
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in context construction for 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(true, Instant.now().toEpochMilli(), VALID_NAV_STATE);
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertEquals("session.started", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION_NAV_ERROR");
    }

    // When reused for negative scenarios, we modify the command parameters based on the Given context.
    // However, Cucumber executes specific Given/When/Then chains. 
    // We will use specific When methods or inspect state to determine the failure mode. 
    // For simplicity in this structure, we assume the specific 'When' for negative cases uses a helper.

    @When("the StartSessionCmd command is executed with invalid context")
    public void theStartSessionCmdCommandIsExecutedWithInvalidContext() {
        // Determine failure mode based on aggregate ID set in 'Given' methods
        String id = aggregate.id();
        boolean authenticated = true;
        long lastActivity = Instant.now().toEpochMilli();
        String navState = VALID_NAV_STATE;

        if (id.equals("SESSION_AUTH_FAIL")) {
            authenticated = false;
        } else if (id.equals("SESSION_TIMEOUT")) {
            // Set timestamp way back in time > 15 mins
            lastActivity = Instant.now().minusSeconds(1000).toEpochMilli();
        } else if (id.equals("SESSION_NAV_ERROR")) {
            navState = ""; // Invalid state
        }
        executeCommand(authenticated, lastActivity, navState);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain rule violation exception, got: " + caughtException.getClass().getSimpleName()
        );
    }

    // --- Helpers ---

    private void executeCommand(boolean isAuthenticated, long lastActivity, String navState) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                VALID_TELLER_ID,
                VALID_TERMINAL_ID,
                isAuthenticated,
                lastActivity,
                navState
            );
            
            var events = aggregate.execute(cmd);
            // Unchecked cast is safe here as we know the command type
            resultEvents = (List<SessionStartedEvent>) (List<?>) events;
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }
}
