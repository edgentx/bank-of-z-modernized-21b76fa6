package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private static final String TEST_TELLER_ID = "TELLER_001";
    private static final String TEST_TERMINAL_ID = "TERM_A01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("SESSION_NO_AUTH");
        // The violation is simulated by providing a null/blank tellerId in the step below,
        // but we could configure the aggregate to reject starts if it's in a bad state.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT");
        // To simulate this violation scenario effectively for a 'StartSession' command,
        // we assume the aggregate might be in a state where it thinks it was active but timed out,
        // or we rely on the command handler to check for an existing valid session.
        // Here we just ensure the aggregate is in a state where the command might fail.
        // Since we are implementing Start, the failure usually implies a session is already active.
        // However, the scenario implies the invariant check.
        // We'll rely on the command setup to trigger the logic.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("SESSION_BAD_NAV");
        aggregate.markNavigableInvalid();
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
        // Default to valid IDs for the success scenario
        executeCmd(TEST_TELLER_ID, TEST_TERMINAL_ID);
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        executeCmd(null, TEST_TERMINAL_ID);
    }

    @When("the StartSessionCmd command is executed on invalid context")
    public void theStartSessionCmdCommandIsExecutedOnInvalidContext() {
        executeCmd(TEST_TELLER_ID, TEST_TERMINAL_ID);
    }

    private void executeCmd(String tellerId, String terminalId) {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Could be IllegalArgumentException or IllegalStateException depending on implementation
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
