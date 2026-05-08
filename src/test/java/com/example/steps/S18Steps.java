package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // ID is arbitrary for the test
        aggregate = new TellerSessionAggregate("session-123");
        // For the success scenario, we assume pre-conditions are met (e.g. authenticated).
        // This sets the stage for Scenario 1.
        aggregate.markAuthenticated(); 
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        // Default state is !authenticated, so we do nothing.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        aggregate.markAuthenticated(); // Authenticated
        aggregate.markExpired();        // But timed out (Simulated)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        aggregate.markAuthenticated(); // Authenticated
        aggregate.invalidateContext();  // But context is invalid
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // The command is constructed in the 'When' step, 
        // but we could stash the ID here if needed. 
        // For this test, we'll just use a literal constant in the When step.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Using hardcoded valid values for the "Given valid..." scenarios
        // or just generic values if the violation is in the Aggregate state.
        // Scenario 4 violation is in the Aggregate, so command data can be valid.
        cmd = new StartSessionCmd("session-123", "teller-42", "term-01");
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-01", event.terminalId());
        
        // Ensure no exception was thrown
        assertNull(thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // The aggregate throws IllegalStateException for domain rule violations
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        
        // We can optionally check the message matches the specific scenario
        // Given we have 3 different rejection scenarios, we check the message.
        assertTrue(thrownException.getMessage().contains("must") || thrownException.getMessage().contains("timeout") || thrownException.getMessage().contains("context"));
    }
}