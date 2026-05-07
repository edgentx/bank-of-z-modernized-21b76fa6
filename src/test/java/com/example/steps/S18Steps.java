package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-42";
    private Instant authTime = Instant.now();
    private String validContext = "CICS";

    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }
    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Defaults in class fields are valid
    }
    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Defaults in class fields are valid
    }
    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, authTime, validContext);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // Scenario 2: Rejected - Authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Auth time is null
        authTime = null;
    }
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Scenario 3: Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // We simulate a pre-existing active session that is now expired
        aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into an active but 'old' state. 
        // Since we can't easily set private fields without reflection, we rely on the logic.
        // But the logic checks `active` flag. 
        // To test this specific invariant strictly via the aggregate methods, we would need to reload state.
        // Given the simple aggregate structure, we simulate by assuming the aggregate holds old state.
        // For this BDD, we'll manually create a command that represents a retry on a logically expired ID
        // OR we modify the test setup to set the flag if possible.
        // However, since TellerSession is new in this story, we might not have a 'Load' method yet.
        // Workaround: The check `if (this.active && isExpired())` implies we need to set active=true.
        // Since we can't, we will mock the violation by creating a command that is INVALID in another way if we can't hit timeout.
        // WAIT: The prompt says "Given a TellerSession aggregate that violates..."
        // We can use reflection or just accept that the `startSession` logic checks `active`.
        // To fully satisfy the requirement without reflection: we assume the aggregate handles hydration.
        // Since we lack a hydrator, we will rely on the fact that the Domain Logic throws the error if the conditions are met.
        // Let's use reflection to force the `active` and `lastActivityAt` fields for the sake of the specific test scenario.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("active");
            field.setAccessible(true);
            field.setBoolean(aggregate, true);
            
            var timeField = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            timeField.setAccessible(true);
            // Set activity to 20 minutes ago (Timeout is 15)
            timeField.set(aggregate, Instant.now().minusSeconds(1200));
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed: reflection error", e);
        }
    }

    // Scenario 4: Rejected - Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Context is invalid (blank)
        validContext = ""; 
    }
}