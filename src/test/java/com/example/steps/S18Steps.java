package com.example.steps;

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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-001";
    private static final String TERMINAL_ID = "terminal-A";

    // Scenario: Successfully execute StartSessionCmd

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure it's in a clean state
        aggregate.setAuthenticated(false);
        aggregate.setActive(false);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command construction for success scenario
        if (command == null) {
            command = new StartSessionCmd(
                    SESSION_ID,
                    TELLER_ID,
                    TERMINAL_ID,
                    true, // authenticated
                    "HOME",
                    Instant.now()
            );
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(TELLER_ID, event.tellerId());
        Assertions.assertEquals(TERMINAL_ID, event.terminalId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Prepare a command where authenticated is false
        command = new StartSessionCmd(
                SESSION_ID,
                TELLER_ID,
                TERMINAL_ID,
                false, // NOT authenticated
                "HOME",
                Instant.now()
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(thrownException.getMessage().contains("authenticated"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set aggregate to active but expired in the past
        aggregate.setActive(true);
        aggregate.setSessionTimeoutAt(Instant.now().minusSeconds(60)); // Expired

        command = new StartSessionCmd(
                SESSION_ID,
                TELLER_ID,
                TERMINAL_ID,
                true,
                "HOME",
                Instant.now()
        );
    }

    // Scenario: StartSessionCmd rejected — Navigation state

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Prepare a command with invalid context (blank terminal or nav state)
        command = new StartSessionCmd(
                SESSION_ID,
                TELLER_ID,
                "", // Invalid terminal
                true,
                "", // Invalid nav state
                Instant.now()
        );
    }
}