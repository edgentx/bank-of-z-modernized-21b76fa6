package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import com.example.domain.uimodel.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private SessionStartedEvent lastEvent;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAsTimeout(); // Force state to timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setInvalidNavigationContext(); // Force invalid context
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context handled in When
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context handled in When
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Determine permissions based on the scenario state.
        // If aggregate is null or specific invalid state, adjust permissions to trigger logic if needed,
        // but the invariants in the Aggregate handle the checks.
        Set<String> perms = Set.of("ROLE_TELLER");
        
        // For the "Authenticated" violation, the aggregate logic checks permissions. 
        // We ensure we pass valid perms here, so the fail must come from the aggregate internal state if tested that way,
        // OR we pass empty perms to trigger the specific invariant.
        // The Gherkin says "aggregate violates...". The aggregate checks permissions in the command.
        // Let's assume valid perms unless we want to test the command validation.
        // "A teller must be authenticated" -> Command must have permissions.
        
        // Actually, looking at the Gherkin: "Given a TellerSession aggregate that violates... A teller must be authenticated"
        // This usually implies the *Command* is the violation source (not authenticated), OR the Aggregate state prevents it.
        // My impl checks: permissions.isEmpty(). 
        
        // Scenario 1 (Valid): Valid perms
        // Scenario 2 (Auth violation): We will simulate by passing empty permissions in the command, as the aggregate accepts the command.
        // BUT, the step says "aggregate violates". My impl logic is inside execute(cmd). 
        // If I want to test the aggregate strictly, I would pass a valid command, but the aggregate throws? 
        // My impl logic: if (cmd.permissions.isEmpty()) throw. 
        // So I should pass empty perms here to satisfy the scenario condition via the command interface.
        
        if (aggregate.getId().equals("session-auth-fail")) {
            perms = Set.of(); // No permissions -> Unauthenticated
        }

        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(), 
                "teller-01", 
                "term-01", 
                perms
            );
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (SessionStartedEvent) events.get(0);
            }
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(lastEvent, "Event should not be null");
        assertEquals("session.started", lastEvent.type());
        assertEquals(aggregate.id(), lastEvent.aggregateId());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should have been thrown");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
