package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerrsession.model.EndSessionCmd;
import com.example.domain.tellerrsession.model.SessionEndedEvent;
import com.example.domain.tellerrsession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Default valid config: Authenticated, Recent Activity, Home State
        this.aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(30));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-invalid-auth", Duration.ofMinutes(30));
        this.aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout", Duration.ofMinutes(30));
        // Set activity to 1 hour ago to violate 30 min timeout
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-error", Duration.ofMinutes(30));
        // Set state to something other than HOME (assumed valid context)
        this.aggregate.setCurrentNavigationState("TRANSACTION_PENDING");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is implicitly handled by the aggregate instantiation in the Given steps
        // No additional action required for this step, but keeping it for Gherkin completeness.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Depending on specific violation, we might check message or type.
        // The scenarios generally map to IllegalStateException in the aggregate.
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
