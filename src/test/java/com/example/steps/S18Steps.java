package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Defaults for valid command
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_3270_01";
    private static final long VALID_TIMEOUT = 900000; // 15 mins
    private static final String VALID_CONTEXT = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION_01");
        this.caughtException = null;
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // State held in context variables for command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // State held in context variables
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("SESSION_TIMEOUT_FAIL");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        this.aggregate = new TellerSessionAggregate("SESSION_NAV_FAIL");
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd;

        // Determine the state of the aggregate or context to build the correct 'bad' command
        // based on the Gherkin setup. We infer the intent from the aggregate ID prefix in the Given steps.
        String id = aggregate.id();

        if (id.contains("AUTH_FAIL")) {
            // Authenticated = false
            cmd = new StartSessionCmd(id, VALID_TELLER_ID, VALID_TERMINAL_ID, false, VALID_TIMEOUT, VALID_CONTEXT);
        } else if (id.contains("TIMEOUT_FAIL")) {
            // Timeout <= 0
            cmd = new StartSessionCmd(id, VALID_TELLER_ID, VALID_TERMINAL_ID, true, 0, VALID_CONTEXT);
        } else if (id.contains("NAV_FAIL")) {
            // Context is blank/null
            cmd = new StartSessionCmd(id, VALID_TELLER_ID, VALID_TERMINAL_ID, true, VALID_TIMEOUT, "");
        } else {
            // Valid Command
            cmd = new StartSessionCmd(id, VALID_TELLER_ID, VALID_TERMINAL_ID, true, VALID_TIMEOUT, VALID_CONTEXT);
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Result events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(
                caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
