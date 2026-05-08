package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure auth for happy path
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Implicitly handled by command construction in 'When'
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Implicitly handled by command construction in 'When'
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Defaults to not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        // Simulate active session that has timed out
        // Note: The logic checks (isActive && idle > timeout). 
        // To hit the specific invariant check for rejection on start, 
        // we rely on the business logic. If the logic simply says "Cannot start because timed out",
        // we might need to simulate a restart attempt on a timed-out ID. 
        // However, based on the invariant text, let's assume we are setting up a state 
        // where the session *would be* considered invalid/timed out or the check fails.
        
        // Actually, for a START command, the aggregate is usually new or inactive. 
        // Let's interpret the violation as: We try to start, but the system detects a conflict 
        // or the user is essentially locked out due to previous timeout. 
        // Or simpler: The Aggregate tracks a "LastActive" that is ancient.
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.setInvalidNavigationState();
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Using fixed valid IDs for simplicity, scenarios don't specify dynamic values
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("terminal-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // We check for IllegalStateException as the domain error implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
