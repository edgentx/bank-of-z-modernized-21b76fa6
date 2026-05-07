package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Setup a valid aggregate by simulating a successful start
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Manually hydrate to a valid state to simulate an active session without invoking Start logic here
        aggregate.execute(new StartSessionCmd("SESSION-123", "TELLER-1", "MAIN_MENU"));
        aggregate.clearEvents(); // Clear the start events so we only verify EndSession events
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate initialization
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create an aggregate that is not authenticated
        aggregate = new TellerSessionAggregate("SESSION-404");
        // Assume state where isAuthenticated is false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Hydrate to active, then force timeout state via test constructor/factory or protected state
        aggregate.execute(new StartSessionCmd("SESSION-TIMEOUT", "TELLER-1", "MAIN_MENU"));
        aggregate.simulateTimeoutForTesting();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("SESSION-NAV-ERR");
        aggregate.forceInvalidNavigationStateForTesting();
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd("SESSION-123", "REASON_NORMAL");
            // Adjust ID for the specific test case contexts if needed, usually we match the aggregate ID
            if (aggregate.id().equals("SESSION-404")) cmd = new EndSessionCmd("SESSION-404", "REASON_TEST");
            if (aggregate.id().equals("SESSION-TIMEOUT")) cmd = new EndSessionCmd("SESSION-TIMEOUT", "REASON_TEST");
            if (aggregate.id().equals("SESSION-NAV-ERR")) cmd = new EndSessionCmd("SESSION-NAV-ERR", "REASON_TEST");

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("SESSION-123", event.aggregateId());
        Assertions.assertEquals("session.ended", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted2() {
        aSessionEndedEventIsEmitted();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError2() {
        theCommandIsRejectedWithADomainError();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError3() {
        theCommandIsRejectedWithADomainError();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError4() {
        theCommandIsRejectedWithADomainError();
    }
}
