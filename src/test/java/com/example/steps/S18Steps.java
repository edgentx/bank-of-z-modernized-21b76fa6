package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Valid test data constants
    private static final String VALID_TELLER_ID = "TELLER_123";
    private static final String VALID_TERMINAL_ID = "TERM_01";
    private static final String VALID_CONTEXT = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION_01");
        this.thrownException = null;
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in command construction in 'When'
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in command construction in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command construction
        if (cmd == null) {
            cmd = new StartSessionCmd(
                "SESSION_01",
                VALID_TELLER_ID,
                VALID_TERMINAL_ID,
                true, // authenticated
                Instant.now(),
                VALID_CONTEXT
            );
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("SESSION_01", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
        // Prepare command with authenticated = false
        this.cmd = new StartSessionCmd(
            "SESSION_AUTH_FAIL",
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            false, // Not authenticated
            Instant.now(),
            VALID_CONTEXT
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("SESSION_TIMEOUT");
        // Prepare command with old authentication time (> 30 mins)
        Instant pastTime = Instant.now().minus(Duration.ofMinutes(45));
        this.cmd = new StartSessionCmd(
            "SESSION_TIMEOUT",
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            pastTime, // Too old
            VALID_CONTEXT
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("SESSION_NAV_BAD");
        // Prepare command with blank context
        this.cmd = new StartSessionCmd(
            "SESSION_NAV_BAD",
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            Instant.now(),
            "" // Invalid context
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(thrownException.getMessage().length() > 0);
    }

}
