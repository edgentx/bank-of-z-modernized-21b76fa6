package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        UUID sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        aggregate.markAuthenticated();
        aggregate.setActive(true);
        // Set last activity to now so it's not timed out
        aggregate.setLastActivityAt(Instant.now()); 
        aggregate.setTimeoutDuration(Duration.ofMinutes(30));
        aggregate.setNavigationState("HOME");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is already set in the aggregate constructor via the 'Given' step.
        // We just verify it's here.
        Assertions.assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(UUID.fromString(aggregate.id()));
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        UUID sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Violation
        aggregate.setNavigationState("HOME");
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setTimeoutDuration(Duration.ofMinutes(30));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        UUID sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set activity to 2 hours ago, timeout to 30 mins -> Timed out
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setTimeoutDuration(Duration.ofMinutes(30));
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        UUID sessionId = UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setTimeoutDuration(Duration.ofMinutes(30));
        aggregate.setNavigationState("UNKNOWN"); // Violation
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // In Java domain logic, we typically throw IllegalStateException or IllegalArgumentException for invariants.
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
