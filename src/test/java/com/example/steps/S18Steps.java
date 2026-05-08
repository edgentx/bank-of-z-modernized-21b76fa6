package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in construction of command in @When
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in construction of command in @When
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command
        if (cmd == null) {
            cmd = new StartSessionCmd("session-1", "teller-123", "terminal-A", true, "HOME");
        }
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
        assertEquals("session.started", event.type());
        assertEquals("session-1", event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-2");
        cmd = new StartSessionCmd("session-2", "teller-123", "terminal-A", false, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Manually force state to simulate timeout check logic (simplified for BDD)
        // In the execute logic, we check isActive. If we assume the session is already active but old.
        // However, StartSession usually transitions from NONE to ACTIVE.
        // To trigger the logic in the `startSession` method:
        // We assume the command implies a resume or re-auth context that might be stale.
        // For this specific story, the logic exists in startSession: `if (isActive ... expired)`
        // So we need the aggregate to be Active and Expired.
        aggregate = repository.findById("session-3"); // Not in repo yet
        aggregate = new TellerSessionAggregate("session-3") {
            @Override
            public List<DomainEvent> execute(com.example.domain.shared.Command c) {
                // Force the state that triggers the check
                this.markActive(); // Custom helper needed or we rely on the logic flow.
                // Actually, let's just call the parent and set state via a 'hidden' backdoor or prior event.
                // Since this is a unit test wrapped in cucumber, we can modify the aggregate if we had a setter.
                // We added a helper `setLastActivityToYesterday` to the aggregate for this test.
                this.setLastActivityToYesterday();
                return super.execute(c);
            }
        };
        
        // Re-instantiate cleanly for the test using the public helper we added to TellerSessionAggregate
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.setLastActivityToYesterday();
        
        cmd = new StartSessionCmd("session-3", "teller-123", "terminal-A", true, "HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-4");
        cmd = new StartSessionCmd("session-4", "teller-123", "terminal-A", true, "INVALID_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}