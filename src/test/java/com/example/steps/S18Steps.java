package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private StartSessionCmd validCmd;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("session-1");
        // Default to authenticated for the happy path
        aggregate.markAuthenticated(); 
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Setup valid command data
        Instant timeout = Instant.now().plusSeconds(3600);
        validCmd = new StartSessionCmd("session-1", "teller-123", "term-A", "MAIN_MENU", timeout);
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in previous step for simplicity, or extend command here
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // If we haven't constructed a valid cmd in the Given steps, create a default one now
            // to ensure the When step is robust across scenarios
            if (validCmd == null) {
                 validCmd = new StartSessionCmd("session-1", "teller-123", "term-A", "MAIN_MENU", Instant.now().plusSeconds(3600));
            }
            resultEvents = aggregate.execute(validCmd);
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
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSession("session-2");
        aggregate.markUnauthenticated(); // Enforce violation
        // Create a valid command structurally, authentication is an aggregate state check
        validCmd = new StartSessionCmd("session-2", "teller-123", "term-A", "MAIN_MENU", Instant.now().plusSeconds(3600));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("session-3");
        aggregate.markAuthenticated();
        // Create command with invalid timeout (past)
        validCmd = new StartSessionCmd("session-3", "teller-123", "term-A", "MAIN_MENU", Instant.now().minusSeconds(10));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSession("session-4");
        aggregate.markAuthenticated();
        // Create command with blank nav state
        validCmd = new StartSessionCmd("session-4", "teller-123", "term-A", "", Instant.now().plusSeconds(3600));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Depending on how we enforce invariants (IllegalStateException vs IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
