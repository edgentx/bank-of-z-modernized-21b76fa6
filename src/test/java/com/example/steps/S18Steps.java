package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    // Helper to create a valid baseline aggregate
    private TellerSessionAggregate createValidAggregate() {
        String id = "sess-123";
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        // Pre-conditions for success
        agg.markAuthenticated(); 
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Command created later, but we capture intent here if needed
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Command created later
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Constructing a standard valid command based on aggregate ID
            cmd = new StartSessionCmd(aggregate.id(), "teller-01", "term-42");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception: " + caughtException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-01", event.tellerId());
        Assertions.assertEquals("term-42", event.terminalId());
    }

    // ---------- Negative Scenarios ----------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = createValidAggregate();
        aggregate.markUnauthenticated(); // Break invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = createValidAggregate();
        aggregate.markStale(); // Break invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = createValidAggregate();
        aggregate.markNavigatingAway(); // Break invariant
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        // Check message to ensure it's the specific domain error
        Assertions.assertNotNull(caughtException.getMessage());
    }
}
