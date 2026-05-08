package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {
    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId implicitly "session-123" from the aggregate setup
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd("session-123");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markUnauthenticated(); // Mutator for test setup
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markStale(); // Mutator for test setup
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markContextInvalid(); // Mutator for test setup
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Should be a domain error");
    }
}
