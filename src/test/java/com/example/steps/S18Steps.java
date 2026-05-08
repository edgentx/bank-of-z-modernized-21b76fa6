package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import com.example.domain.uimodel.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid state for success case
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(sessionId);
        // 'authenticated' defaults to false
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Needs auth first
        aggregate.expireSession();      // Force timeout
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.invalidateNavigationState(); // Force invalid nav state
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context for the command
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context for the command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(sessionId, "teller-01", "terminal-01");
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // The aggregate throws IllegalStateException for invariant violations
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
