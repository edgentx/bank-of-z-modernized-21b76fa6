package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Throwable capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateWhereTellerIsNotAuthenticated() {
        // Violation: Unauthenticated context (Null Teller)
        aggregate = new TellerSessionAggregate("SESSION-ERR-AUTH");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatHasTimedOut() {
        // Violation: Stale context (Is Active = false)
        aggregate = new TellerSessionAggregate("SESSION-ERR-TIMEOUT");
        aggregate.markStale(); // Force state violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithInvalidNavigationState() {
        // Violation: Invalid Context (Nav State Locked)
        aggregate = new TellerSessionAggregate("SESSION-ERR-NAV");
        aggregate.lockNavigation(); // Force state violation
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Data setup for the command happens in the When step for atomicity
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Data setup for the command happens in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Using defaults for valid command (Alice, T1)
            Command cmd = new StartSessionCmd(aggregate.id(), "TELLER-ALICE", "TERM-01");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), got: " + capturedException.getClass().getSimpleName()
        );
    }
}
