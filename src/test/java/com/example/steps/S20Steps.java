package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {
    private TellerSessionAggregate aggregate;
    private List<SessionEndedEvent> events;
    private Exception exception;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setTimedOut(false);
        aggregate.setNavigationStateValid(true);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization in previous step
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var result = aggregate.execute(new EndSessionCmd("session-123"));
            this.events = result.stream()
                    .filter(e -> e instanceof SessionEndedEvent)
                    .map(e -> (SessionEndedEvent) e)
                    .toList();
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(exception, "Expected no exception, but got: " + exception.getMessage());
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("session.ended", events.get(0).type());
        Assertions.assertEquals("session-123", events.get(0).aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setActive(true);
        aggregate.setTimedOut(false);
        aggregate.setNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setTimedOut(true); // Violation
        aggregate.setNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setTimedOut(false);
        aggregate.setNavigationStateValid(false); // Violation
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(exception, "Expected an exception to be thrown");
        Assertions.assertTrue(exception instanceof IllegalStateException, "Expected IllegalStateException, got " + exception.getClass().getSimpleName());
        Assertions.assertFalse(exception.getMessage().isBlank(), "Exception message should not be blank");
    }
}
