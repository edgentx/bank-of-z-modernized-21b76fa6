package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> result;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setActive("teller-1", "IDLE");
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        command = new EndSessionCmd("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-no-auth");
        aggregate.setInactive(); // No teller authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setActive("teller-1", "IDLE");
        // Set activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivity(Instant.now().minusSeconds(31 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.setActive("teller-1", "CRITICAL_TRANSACTION_PENDING");
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        if (command == null) {
            command = new EndSessionCmd(aggregate.id());
        }
        try {
            result = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertEquals(1, result.size(), "Should have one event");
        Assertions.assertTrue(result.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Ideally catch specific DomainError, but standard Exceptions are used in this proto-implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException);
    }
}
