package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup handled in the 'When' step for encapsulation
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup handled in the 'When' step for encapsulation
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // For this story, we simulate an unauthenticated state via the command context.
        // The aggregate will enforce this invariant.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        // The aggregate checks internal state. If we need to simulate an expired/stale state
        // we would apply events here, but since we are STARTING the session, this invariant
        // typically ensures the supplied parameters (like timeout config) are valid.
        // For this test, we assume the aggregate enforces a minimum timeout > 0.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        // Context setup handled in the 'When' step
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Determine context based on the scenario implied by the Given step
            String id = aggregate.id();
            Command cmd;
            
            if (id.contains("auth-fail")) {
                cmd = new StartSessionCmd(id, "teller-1", "terminal-1", false, 30, "MAIN_MENU");
            } else if (id.contains("timeout")) {
                cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, -5, "MAIN_MENU");
            } else if (id.contains("nav-fail")) {
                cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, 30, null);
            } else {
                cmd = new StartSessionCmd(id, "teller-1", "terminal-1", true, 30, "MAIN_MENU");
            }

            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Verify it is a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
