package com.example.steps;

import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private StartSessionCmd command;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateWithUnauthenticatedTeller() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate a session that exists but the teller is not marked authenticated in context
        // For this command, the authentication is passed via the command context.
        // We will create a command with null or empty auth token in the 'And' step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup: Simulate that the previous session ended too recently (cooldown violation)
        aggregate.markLastSessionEnded(Instant.now().minus(Duration.ofMinutes(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup: Force the aggregate into a state where it thinks it's already busy
        aggregate.markNavigationInconsistent();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Command constructed with valid ID
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Command constructed with valid ID
    }

    // Specific step for the unauthenticated scenario
    @And("the teller is not authenticated")
    public void theTellerIsNotAuthenticated() {
        // We will use this in the When step to construct the bad command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Determine context based on previous Givens. Heuristic:
        // If the aggregate is marked inconsistent, we might need a specific command flag, 
        // but usually the invariant depends on the Aggregate's state.
        // However, for Auth, it depends on the Command.
        
        String tellerId = "teller-101";
        String terminalId = "term-Alpha";
        String authToken = "valid-token-xyz";
        
        // Detect "unauthenticated" scenario by checking if we explicitly set a flag or null token
        // Since Cucumber steps don't pass state easily, we check the aggregate state.
        // Actually, the aggregate state doesn't store the incoming command's auth.
        // Let's assume the unauthenticated scenario is triggered by a null/empty token.
        // But how do we know? We'll just try to execute a valid one and catch if the setup demands invalid.
        // BETTER APPROACH: The step 'A teller must be authenticated' implies the command lacks auth.
        // We will check the aggregate state. If it's the base state, we send valid auth.
        // If the specific violation setup was called, we send invalid auth.
        
        // Since we can't easily share state between steps without a shared context object,
        // we will instantiate the command here based on defaults.
        
        boolean isAuthTest = (aggregate.getLastSessionEndTime() == null && !aggregate.isNavigationInconsistent());
        
        if (isAuthTest) {
            // This is the "unauthenticated" test case based on our setup logic not setting timeout
            authToken = null; // Violate invariant
        }

        command = new StartSessionCmd(aggregate.id(), tellerId, terminalId, authToken);

        try {
            resultEvents = aggregate.execute(command);
            // Apply events to update state for verification
            resultEvents.forEach(e -> {
                if (e instanceof SessionStartedEvent sse) {
                    // In a real app, we'd apply this to a new instance, but here we just verify return
                }
            });
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Verify it's an IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(IllegalArgumentException.class.isInstance(capturedException) || IllegalStateException.class.isInstance(capturedException));
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedWithDefaults() {
        // Default execution for successful path
        command = new StartSessionCmd(aggregate.id(), "teller-101", "term-Alpha", "token-123");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
