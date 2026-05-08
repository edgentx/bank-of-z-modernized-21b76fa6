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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // State setup helpers
    private boolean isAuthenticated = true;
    private Duration timeoutDuration = Duration.ofMinutes(30); // Configured default
    private boolean navigationStateValid = true;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "sess-" + Instant.now().toEpochMilli();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @And("the teller is authenticated")
    public void theTellerIsAuthenticated() {
        this.isAuthenticated = true;
    }

    @And("the session timeout is within configured limits")
    public void theSessionTimeoutIsWithinConfiguredLimits() {
        this.timeoutDuration = Duration.ofMinutes(30);
    }

    @And("the navigation state is valid")
    public void theNavigationStateIsValid() {
        this.navigationStateValid = true;
    }

    // Negative Preconditions

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aValidTellerSessionAggregate();
        this.isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aValidTellerSessionAggregate();
        // Simulating a violation of timeout configuration limits
        this.timeoutDuration = Duration.ofMinutes(-1); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aValidTellerSessionAggregate();
        this.navigationStateValid = false;
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Construct command with valid IDs (preconditions are handled via aggregate state/context in real scenario,
            // but here we pass them to the command which validates them)
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                "teller-123",
                "term-TN3270-01",
                this.isAuthenticated,
                this.timeoutDuration,
                this.navigationStateValid
            );
            
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException, 
            "Expected a domain error (IllegalStateException/IllegalArgumentException)");
    }
}
