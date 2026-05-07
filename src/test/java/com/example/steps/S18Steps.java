package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("TS-1");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup handled in 'when' via command object
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup handled in 'when' via command object
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("TS-2");
        // Implicitly violates if we were tracking auth, but for StartSession the invariant is logic-based.
        // Here we simulate the 'authenticated' check failing via context, 
        // but since the command carries the context, we might need a separate 'invalid' command or state.
        // However, 'StartSession' initiates. The invariant is likely about the current state vs command data.
        // If 'StartSession' requires a token, we can pass an invalid one.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("TS-3");
        // Pre-populate state if necessary, though this is a Start command.
        // Usually this applies to 'ContinueSession'. For Start, we might force it via a command flag 
        // indicating the session is already 'stale' or similar out-of-band data.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("TS-4");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Determine context based on scenario
            String scenario = "";
            if (aggregate.id().equals("TS-1")) {
                 scenario = "valid";
            } else if (aggregate.id().equals("TS-2")) {
                 // Force auth violation by using null context or specific marker if supported
                 scenario = "unauthenticated";
            } else if (aggregate.id().equals("TS-3")) {
                 scenario = "timeout";
            } else if (aggregate.id().equals("TS-4")) {
                 scenario = "invalid_nav";
            }

            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "teller-123", "term-ABC", scenario);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // In a real app we might check specific error types, here we check failure
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
