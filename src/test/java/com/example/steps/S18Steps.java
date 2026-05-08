package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd.StartSessionCmdBuilder cmdBuilder;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "sess-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(sessionId)
                .tellerId("teller-001")
                .terminalId("term-42")
                .isAuthenticated(true)
                .initialContext("MAIN_MENU")
                .lastActivityTimestamp(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Default builder setup covers this, noop here
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Default builder setup covers this, noop here
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "sess-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(sessionId)
                .tellerId("teller-bad")
                .terminalId("term-bad")
                .isAuthenticated(false) // Violation
                .initialContext("MAIN_MENU")
                .lastActivityTimestamp(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 20 minutes ago (Timeout policy is 15)
        Instant staleTimestamp = Instant.now().minus(Duration.ofMinutes(20));
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(sessionId)
                .tellerId("teller-slow")
                .terminalId("term-slow")
                .isAuthenticated(true)
                .initialContext("MAIN_MENU")
                .lastActivityTimestamp(staleTimestamp); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        String sessionId = "sess-nav-bad";
        aggregate = new TellerSessionAggregate(sessionId);
        cmdBuilder = StartSessionCmd.builder()
                .sessionId(sessionId)
                .tellerId("teller-lost")
                .terminalId("term-lost")
                .isAuthenticated(true)
                .initialContext("") // Violation: Blank/Invalid context
                .lastActivityTimestamp(Instant.now());
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = cmdBuilder.build();
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        
        // Verify aggregate state mutation
        assertEquals(TellerSessionAggregate.SessionState.STARTED, aggregate.getState());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Verify no events were committed
        assertTrue(aggregate.uncommittedEvents().isEmpty() || resultEvents == null || resultEvents.isEmpty());
    }
}