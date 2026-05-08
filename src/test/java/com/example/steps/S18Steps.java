package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String providedTellerId;
    private String providedTerminalId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Pre-authenticate for success scenario
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        providedTellerId = "teller-123";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        providedTerminalId = "term-TX-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), providedTellerId, providedTerminalId);
            // Reload from repo to ensure we are testing persistence hydration logic if applicable, 
            // though in pure unit test direct usage is fine. Here we use instance var.
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(providedTellerId, event.tellerId());
        assertEquals(providedTerminalId, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Intentionally do NOT mark as authenticated
        repository.save(aggregate);
        aValidTellerIdIsProvided();
        aValidTerminalIdIsProvided();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAuthenticated();
        aggregate.setLastActivityToExpired(); // Force invariant violation
        repository.save(aggregate);
        aValidTellerIdIsProvided();
        aValidTerminalIdIsProvided();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        repository.save(aggregate);
        aValidTellerIdIsProvided();
        // Setting terminalId to null to trigger context validation failure
        providedTerminalId = null; 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect IllegalStateException or IllegalArgumentException based on our implementation
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
