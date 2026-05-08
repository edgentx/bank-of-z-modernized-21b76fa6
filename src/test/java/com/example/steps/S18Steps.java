package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Set;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command executedCommand;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    // State builders to simulate 'Given a TellerSession aggregate that violates...'
    // We use static configuration for specific violations
    
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // In a real framework this would set a context variable. 
        // For simplicity here, we assume the 'When' step constructs the command with valid defaults.
        // This step is effectively a no-op or a setup marker for readability.
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above.
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command construction
        executeCommand("user-01", "term-01", Set.of("TELLER"), System.currentTimeMillis(), "IDLE");
    }

    // --- Violation Handlers ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    // --- Context-Aware Execution ---
    // Since Cucumber steps don't pass arguments directly between Given/When without storage,
    // we inspect the Aggregate state or ID to infer the scenario context (mocking a context object).
    
    private void executeCommand(String teller, String terminal, Set<String> roles, long lastActivity, String navState) {
        // Determine if we are in a violation scenario based on the sessionId setup in Given steps
        String id = aggregate.id();
        
        if (id.contains("auth-fail")) {
            // Violate Auth: Empty ID or Empty Roles
            teller = ""; 
            roles = Set.of();
        } else if (id.contains("timeout-fail")) {
            // Violate Timeout: Timestamp is very old
            long oneHourAgo = System.currentTimeMillis() - (3600 * 1000);
            lastActivity = oneHourAgo;
        } else if (id.contains("nav-fail")) {
            // Violate Nav State: State is UNKNOWN/BUSY
            navState = "UNKNOWN";
        }

        StartSessionCmd cmd = new StartSessionCmd(teller, terminal, roles, lastActivity, navState);
        executedCommand = cmd;

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event expected");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("user-01", event.tellerId());
        assertEquals("term-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Depending on the invariant violated, the exception message matches the Gherkin scenario requirement
        assertTrue(
            capturedException.getMessage().contains("authenticated") ||
            capturedException.getMessage().contains("timeout") ||
            capturedException.getMessage().contains("Navigation state") ||
            capturedException.getMessage().contains("IllegalStateException")
        );
    }
}
