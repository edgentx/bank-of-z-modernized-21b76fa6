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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER-101";
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM-3270-A";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Defaults for happy path where specific setups aren't needed
            boolean isAuthenticated = (this.tellerId != null); // Simple proxy for auth if not overridden
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, isAuthenticated);
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
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
        // Auth is false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markLastActivityAsExpired(); // Force the aggregate into an expired state context
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markNavigationInvalid(); // Force navigation state to invalid
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
    }

    // Execution for negative scenarios uses the same When method defined above
    // We just need to override the command creation logic implicitly via state checks in the aggregate,
    // OR we tweak the command flag based on the setup. 
    // To support the 'Violates Auth' case cleanly without changing the 'When' method,
    // we will use a flag in the steps or specific test setup logic.
    
    // Refining the 'Violates Auth' execution path:
    // Since the 'When' method relies on boolean isAuthenticated, we need a way to signal that.
    // We can assume 'tellerId' null means unauthenticated, or use a specific step setup variable.
    // For simplicity in BDD, let's look at the specific violation steps.

    @When("the StartSessionCmd command is executed with unauthenticated flag")
    public void theStartSessionCmdCommandIsExecutedUnauthenticated() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, false);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    // To keep the step names matching the feature file exactly, we will reuse the generic @When
    // but we need to handle the different contexts. The standard Cucumber pattern is 
    // to rely on the aggregate state being set up correctly by the Given.
    
    // Re-mapping the specific 'Violates Auth' given to trigger a specific execution:
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupUnauthenticatedContext() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
        // We flag a field to tell the When step to pass false
        this.isAuthenticated = false;
    }
    
    // Wait, the Feature file uses the exact same "When the StartSessionCmd command is executed" line.
    // So the code logic for the 'When' step must branch based on the 'Given' context.
    
    // Let's update the single 'When' method to be context-aware or default to true
    // unless the 'Given' set a field to false.
    
    private boolean isAuthenticated = true; // Default true

    @Given("a valid TellerSession aggregate")
    public void resetAuthFlag() {
        this.isAuthenticated = true;
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setAuthViolation() {
        this.isAuthenticated = false;
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setTimeoutViolation() {
        this.isAuthenticated = true;
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markLastActivityAsExpired();
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setNavigationViolation() {
        this.isAuthenticated = true;
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markNavigationInvalid();
        this.tellerId = "TELLER-101";
        this.terminalId = "TERM-3270-A";
    }

    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmdGeneric() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, isAuthenticated);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage() != null && !thrownException.getMessage().isBlank());
    }

}
