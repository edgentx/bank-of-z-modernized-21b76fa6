package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinav.model.*;
import com.example.domain.uinav.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("SESSION-INIT");
        // Pre-authenticate the session via the aggregate's internal auth method for testing
        aggregate.authenticate("TELLER-1", "TERMINAL-A"); 
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled implicitly by the StartSessionCmd construction in the When step
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled implicitly by the StartSessionCmd construction in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd("SESSION-INIT", "TELLER-1", "TERMINAL-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSession("SESSION-NO-AUTH");
        // Intentionally do NOT call authenticate()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("SESSION-TIMEOUT");
        // Mark authenticated but expired
        aggregate.markAuthenticated(); 
        aggregate.expireSession(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSession("SESSION-BAD-NAV");
        aggregate.markAuthenticated();
        // Set an invalid navigation state for a start operation
        aggregate.setNavigationState("UNKNOWN_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), but got: " + capturedException.getClass().getSimpleName()
        );
    }

    // Additional context setup for valid scenarios
    @When("the StartSessionCmd command is executed on authenticated aggregate")
    public void theStartSessionCmdCommandIsExecutedOnAuthenticatedAggregate() {
        try {
            aggregate = new TellerSession("SESSION-VALID");
            aggregate.markAuthenticated(); // Simulate successful auth prior to start
            Command cmd = new StartSessionCmd("SESSION-VALID", "TELLER-1", "TERMINAL-A");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
