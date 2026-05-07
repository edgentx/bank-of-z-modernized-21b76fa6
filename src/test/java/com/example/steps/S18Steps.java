package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private String currentTellerId = "teller-123";
    private String currentTerminalId = "term-ABC";
    
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Defaults for success scenario
        aggregate = new TellerSessionAggregate("session-001");
        aggregate.markAuthenticated();
        aggregate.setValidActiveTime();
        aggregate.setValidNavigationState();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "user-42";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "tm-88";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-fail-auth");
        aggregate.markUnauthenticated();
        aggregate.setValidActiveTime();
        aggregate.setValidNavigationState();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-fail-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago (assuming timeout is 15)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setValidNavigationState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-fail-nav");
        aggregate.markAuthenticated();
        aggregate.setValidActiveTime();
        aggregate.setInvalidNavigationState();
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
            // Persist the aggregate state changes (in memory for test)
            repository.save(aggregate);
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
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(currentTellerId, event.tellerId());
        assertEquals(currentTerminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        
        // Ensure no events were emitted
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}