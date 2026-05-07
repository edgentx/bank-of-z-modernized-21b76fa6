package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Test data constants
    private static final String SESSION_ID = "sess-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-42";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in the When step via the command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in the When step via the command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // The violation is in the command context for this rule (not authenticated),
        // but we create the aggregate ready to receive the bad command.
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Simulate a hydrated aggregate that has an old timestamp
        // The timeout logic in the aggregate checks `lastActivityAt` vs `Now`.
        Instant past = Instant.now().minus(Duration.ofMinutes(20)); // 20 mins ago
        // We use the factory helper to set internal state for testing validation logic
        this.aggregate = TellerSessionAggregate.hydrated(SESSION_ID, true, "IDLE", past, false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(true, "IDLE");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        executeCommand(false, "IDLE");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavState() {
        executeCommand(true, "INVALID"); // "INVALID" triggers the specific logic in our aggregate
    }

    private void executeCommand(boolean isAuthenticated, String navState) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, isAuthenticated, navState);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(TELLER_ID, event.tellerId());
        Assertions.assertEquals(TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // We expect IllegalStateException for invariant violations in this domain model
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
