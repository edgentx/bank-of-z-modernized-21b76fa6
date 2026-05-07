package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermemory.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellermemory.repository.TellerSessionRepository;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("SESSION-123");
        aggregate.initializeSession("TELLER-1", "BRANCH-A"); // Prepare state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate context in the step above
        Assertions.assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            // Re-fetch to simulate persistence roundtrip or just use instance
            TellerSession session = repository.findById(aggregate.id()).orElseThrow();
            resultEvents = session.execute(cmd);
            session.clearEvents(); // Simulate commit
            repository.save(session);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSession("SESSION-404");
        // Not initialized (authenticated)
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("SESSION-TIMEOUT");
        aggregate.initializeSession("TELLER-1", "BRANCH-A");
        aggregate.forceTimeoutForTesting(); // Simulate time passing
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSession("SESSION-NAV");
        aggregate.initializeSession("TELLER-1", "BRANCH-A");
        aggregate.corruptNavigationStateForTesting();
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException);
    }
}