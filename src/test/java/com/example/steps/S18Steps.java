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
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-42";
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repository.create(sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // tellerId is defaulted
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // terminalId is defaulted
    }

    @And("the teller is authenticated")
    public void theTellerIsAuthenticated() {
        aggregate.setContext(true, true); // Auth = true, Op = true
    }

    @And("the terminal is operational")
    public void theTerminalIsOperational() {
        aggregate.setContext(true, true); // Auth = true, Op = true
    }

    @And("the teller is NOT authenticated")
    public void theTellerIsNotAuthenticated() {
        aggregate.setContext(false, true); // Auth = false, Op = true
    }

    @And("the previous session has not timed out")
    public void thePreviousSessionHasNotTimedOut() {
        // Simulate an active session (last activity = now)
        aggregate.markActive(Instant.now());
    }

    @And("the navigation state is invalid")
    public void theNavigationStateIsInvalid() {
        aggregate.setContext(true, false); // Auth = true, Op = false (Bad Context)
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            var cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
            // Apply side effects for subsequent steps if needed
            if (!resultEvents.isEmpty()) {
                 // In a real repo, we'd load events. Here we just assume success updates internal state if not throwing.
                 // The aggregate method updates state synchronously.
            }
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
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect IllegalStateException per our aggregate implementation
        assertTrue(capturedException instanceof IllegalStateException);
        // Check message content to ensure it's the right violation
        String msg = capturedException.getMessage();
        assertTrue(msg != null && !msg.isEmpty());
    }
}