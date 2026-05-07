package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to create a valid aggregate state
    private TellerSessionAggregate createValidAggregate() {
        // Assuming constructor takes ID. We set valid state internally for the 'valid' scenario.
        TellerSessionAggregate agg = new TellerSessionAggregate("session-123");
        // In a real repo, we might load this. Here we just instantiate.
        // The aggregate defaults to NOT authenticated and NOT active.
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidAggregate();
        // To make it valid for starting a session, we assume the external context (Spring Security)
        // has verified the user. The aggregate needs to reflect this if it tracks auth.
        // However, based on the error "A teller must be authenticated", and the fact that
        // we are using an aggregate, let's assume the aggregate tracks if the teller is logged in.
        // For this specific step, we simulate a 'ready to start' state.
        // *But*, the aggregate is likely in a 'None' state. The command initiates it.
        // Wait, the invariant says "A teller must be authenticated".
        // Let's assume the aggregate has a `markAuthenticated()` method or similar for setup,
        // OR the command carries the token. Let's assume the aggregate state is clean,
        // but the "Violation" scenarios set specific flags.
        // Actually, if "A teller must be authenticated" is an invariant, the aggregate
        // needs to know the teller is authenticated. Perhaps the `StartSessionCmd`
        // implicitly assumes the teller IS authenticated, and if the aggregate state says otherwise, it fails.
        // Let's assume we set the 'authenticated' flag to true here to pass the happy path.
        aggregate.markAuthenticated(); // Hypothetical internal state setup
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context: handled in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context: handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesATellerMustBeAuthenticated() {
        aggregate = createValidAggregate();
        // Do NOT mark authenticated. The aggregate should be in a default state where isAuthenticated is false.
        // If our constructor defaults to false, we are good.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Depending on implementation, could be IllegalStateException, IllegalArgumentException, or custom.
        // The prompt example used IllegalStateException for invariants.
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionsMustTimeout() {
        aggregate = createValidAggregate();
        aggregate.markAuthenticated();
        // Simulate an existing expired session context if the aggregate was re-used, 
        // or force the state to 'TimedOut'.
        aggregate.markExpired(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = createValidAggregate();
        aggregate.markAuthenticated();
        // Simulate a mismatch or invalid navigation state
        aggregate.invalidateNavigationState();
    }
}
