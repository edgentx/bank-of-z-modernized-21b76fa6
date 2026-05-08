package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-007");
        this.aggregate.setNavigationState("IDLE");
        this.aggregate.setLastActivityAt(Instant.now());
        repository.save(this.aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the aggregate initialization, assuming the command uses the aggregate's ID
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-no-auth");
        // Do not call markAuthenticated
        repository.save(this.aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated("teller-007");
        // Set last activity to 31 minutes ago (Timeout is 30)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        repository.save(this.aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-bad-state");
        this.aggregate.markAuthenticated("teller-007");
        // Set a state that is considered invalid for ending session (e.g., mid-flight transaction)
        this.aggregate.setNavigationState("CRITICAL_OPERATION");
        repository.save(this.aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertFalse(aggregate.isActive(), "Session should be terminated");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // In Java, we often use IllegalStateException or a custom DomainException for business rule violations
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
