package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setTellerId("TELLER-01");
        aggregate.setActive(true);
        aggregate.setCurrentScreen("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate construction in the previous step
        // The command will use the same ID
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id(), "TELLER-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }

    // --- Negative Scenarios Setup ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "TS-401";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulating a session that was never authenticated or lost auth
        aggregate.setActive(false); // Not active
        aggregate.setTellerId(null); // No teller
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "TS-402";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setTellerId("TELLER-01");
        aggregate.setActive(true);
        // Set activity to 31 minutes ago to violate the 30 min timeout configured in aggregate
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "TS-403";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setTellerId("TELLER-01");
        aggregate.setActive(true);
        // Set state to a protected screen that prevents logout
        aggregate.setCurrentScreen("CASH_DRAWER_OPEN");
        aggregate.setLastActivityAt(Instant.now());
    }

}
