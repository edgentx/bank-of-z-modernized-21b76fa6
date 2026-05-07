package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S20Steps {

    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        repo.save(aggregate);
        // Assuming valid state implies authenticated and active
        // For this BDD, we assume the constructor creates a valid base or we would hydrate it
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate ID
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new com.example.domain.tellersession.model.EndSessionCmd(aggregate.id());
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        var events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have events");
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent, "Should be SessionEndedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String id = "session-unauth";
        aggregate = new TellerSessionAggregate(id);
        // Force state to unauthenticated
        aggregate.markUnauthenticated(); // Helper method for test setup
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markTimedOut(); // Helper method for test setup
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String id = "session-bad-nav";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markNavigationInvalid(); // Helper method for test setup
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Should have thrown exception");
        // Could check type if custom exceptions were defined, e.g., DomainException
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
