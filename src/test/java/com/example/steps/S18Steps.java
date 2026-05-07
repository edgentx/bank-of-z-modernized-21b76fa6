package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "TS-AUTH-FAIL";
        // Create an aggregate instance. The violation will be triggered by the command execution context.
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Force internal state to simulate an already active session which implies a context check,
        // or simply rely on the Command execution logic to verify auth tokens.
        // For this test, we will execute the command assuming the context lacks auth.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate an aggregate with a last activity time that is too old
        // In a real scenario, this would be loaded from state, here we assume the aggregate
        // has internal state logic that we can't easily set without a repository builder,
        // so we pass the 'current time' as part of the validation or assume the aggregate handles it.
        // For this unit test, we might rely on the Command to carry the timestamp or mock the clock.
        // Assuming standard execution behavior where we might force the state.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "TS-NAV-ERR";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER-101";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Scenario 1: Success
        if (sessionId.equals("TS-123")) {
            // Valid context simulated
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, true, Instant.now(), "MENU_MAIN");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                thrownException = e;
            }
        }
        // Scenario 2: Auth Failure
        else if (sessionId.equals("TS-AUTH-FAIL")) {
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, false, Instant.now(), "MENU_MAIN");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                thrownException = e;
            }
        }
        // Scenario 3: Timeout (Simulated via old timestamp in command for validation)
        else if (sessionId.equals("TS-TIMEOUT")) {
            // Pass an old timestamp to simulate a session that would be considered timed out
            // if it were a refresh, but here we are starting.
            // The criteria says "Sessions must timeout after...", usually this applies to ongoing sessions.
            // However, if the *start* command is rejected due to timeout config, it implies a check.
            // Let's assume the validation checks for a valid 'current time' vs 'last activity' context.
            // We pass a time that implies inactivity context.
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, true, Instant.now().minus(Duration.ofHours(2)), "INVALID_CTX");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                thrownException = e;
            }
        }
        // Scenario 4: Navigation State
        else if (sessionId.equals("TS-NAV-ERR")) {
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, true, Instant.now(), "UNKNOWN_STATE");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                thrownException = e;
            }
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("TS-123", event.aggregateId());
        assertEquals("TELLER-101", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Checking it's a domain exception (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
