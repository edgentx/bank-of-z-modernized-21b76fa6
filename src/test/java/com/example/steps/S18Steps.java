package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // System State
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test Data
    private static final String VALID_TELLER_ID = "tell_01";
    private static final String VALID_TERMINAL_ID = "term_01";
    private static final String SESSION_ID = "sess_01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Nothing to do here, this implies the command will be created with valid ID later
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Nothing to do here
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session.started", sse.type());
        assertEquals(SESSION_ID, sse.aggregateId());
        assertEquals(VALID_TELLER_ID, sse.tellerId());
        assertEquals(VALID_TERMINAL_ID, sse.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // The violation is simulated by passing a null or blank teller ID in the command step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAsTimedOut(); // Manually force the aggregate into a state that triggers the rejection logic
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAsActive(); // Force Active state so starting again violates invariants
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Exception should be a domain error (IllegalArgument or IllegalState)"
        );
    }

    // --- Helper ---

    private void executeCommand(Command cmd) {
        try {
            // The given constraints for negative scenarios often imply specific command properties or aggregate states.
            // We handle the "Violates Auth" case by modifying the command specifically for that scenario context
            if (cmd instanceof StartSessionCmd && aggregate.getStatus() == TellerSessionAggregate.Status.NONE && aggregate.getTellerId() == null) {
                // Check if this is the "Violates Auth" scenario based on the state setup.
                // Since we can't pass context between Given/When easily without shared state, we rely on the aggregate state setup above.
                // However, the Auth violation is in the Command, not the Aggregate state (usually).
                // Let's inspect the specific scenario logic:
                // The Gherkin says "Given aggregate that violates auth". This is ambiguous.
                // But the logic `tellerId == null` is an auth check.
                // Let's look at the previous method `aTellerSessionAggregateThatViolatesAuth`.
                // It didn't set the aggregate to a bad state, it just prepared it.
                // I will assume that the Auth Violation test uses a bad command.
                
                // Actually, to keep it simple and consistent with the pattern, I'll detect the scenario implicitly or allow the Test Runner to call a different method.
                // But Cucumber maps text to method. The method `theStartSessionCmdCommandIsExecuted` is shared.
                // I will use a flag or just inspect the aggregate.
            }
            
            // Hack to differentiate the negative test for Auth without complicating the parser:
            // If the aggregate is in `NONE` state but we want to test rejection, and it's not the other two states, it must be Auth.
            if (cmd instanceof StartSessionCmd && aggregate.getStatus() == TellerSessionAggregate.Status.NONE && !(cmd instanceof StartSessionCmd)) {
               // Wait, `cmd` IS StartSessionCmd.
               // Let's just check if the aggregate is in a specific "poison" state for the other two, and default the bad command for the first.
            }

            // Refinement:
            // 1. "Violates Auth": We will pass a null tellerId command.
            // 2. "Violates Timeout": We use `markAsTimedOut()`, command is valid.
            // 3. "Violates NavState": We use `markAsActive()`, command is valid.
            
            Command actualCmd = cmd;
            if (aggregate.getStatus() == TellerSessionAggregate.Status.NONE && aggregate.getTellerId() == null) {
                // This is the "Auth Violation" case setup by `aTellerSessionAggregateThatViolatesAuth`
                // (Assuming that setup method didn't put us in TIMEOUT or ACTIVE)
                // We override the command to be bad.
                actualCmd = new StartSessionCmd(null, VALID_TERMINAL_ID);
            }

            resultEvents = aggregate.execute(actualCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

}
