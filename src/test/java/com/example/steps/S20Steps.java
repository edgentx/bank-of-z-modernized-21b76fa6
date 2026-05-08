package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        UUID id = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated("teller-123"); // Valid defaults
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by aggregate creation, but can verify here
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        EndSessionCmd cmd = new EndSessionCmd(UUID.fromString(aggregate.id()), "teller-123");
        try {
            // Reload to ensure we act on persisted state (simulation)
            TellerSessionAggregate agg = repository.findById(UUID.fromString(aggregate.id())).orElseThrow();
            resultEvents = agg.execute(cmd);
            // Save uncommitted events would happen here in a real app
            repository.save(agg);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        UUID id = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markUnauthenticated(); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        UUID id = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated("teller-123");
        aggregate.markTimedOut(); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        UUID id = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated("teller-123");
        aggregate.markNavigationInvalid(); // Violation
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception was not thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
