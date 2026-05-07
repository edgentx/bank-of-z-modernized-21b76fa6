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

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;
    private static final String TEST_SESSION_ID = "session-123";
    private static final String TEST_TELLER_ID = "teller-01";
    private static final String TEST_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markNavigationStateReady(); // Ensure context is valid
        // Assume authenticated by default for 'valid' aggregate
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Stored in constants
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Stored in constants
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAsUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markNavigationStateReady();
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markNavigationStateInvalid(); // Set state to something invalid
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        cmd = new StartSessionCmd(TEST_SESSION_ID, TEST_TELLER_ID, TEST_TERMINAL_ID);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(TEST_TELLER_ID, event.tellerId());
        Assertions.assertEquals(TEST_TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // In Java domain, we usually throw IllegalStateException or IllegalArgumentException for domain invariants
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}