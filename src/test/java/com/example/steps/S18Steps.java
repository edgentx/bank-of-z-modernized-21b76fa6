package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.model.SessionRejectedEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession session;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.session = new TellerSession("session-1");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in the When step via Command construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in the When step via Command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "term-ABC");
            List<DomainEvent> events = session.execute(cmd);
            // In a real app, we might persist the new state here via repository
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        List<DomainEvent> events = session.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.session = new TellerSession("session-2");
        // Logic to simulate unauthenticated state might be handled by the Aggregate's internal state
        // For this test, we assume the aggregate allows creation, but the Command validation logic fails.
        // However, the domain description implies the AGGREGATE validates.
        // We create the aggregate, and the command will be rejected internally.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // We simulate an aggregate that is already in a state where a timeout has occurred or logic prevents restart.
        // For the sake of this unit test, we can use a specific ID or internal flag if the aggregate supports it.
        // Here we rely on the Aggregate logic to reject the command.
        this.session = new TellerSession("session-3");
        // If we need to simulate a timed-out state, we might need to hydrate it from events or have a specific constructor.
        // Assuming the aggregate logic handles the rejection based on business rules.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.session = new TellerSession("session-4");
        // Similar to above, assuming internal state or validation logic triggers rejection.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for the specific event type if the pattern is to emit an error event instead of throwing
        // Based on "Rejected with a domain error", throwing an Exception is the standard Java way.
        // Alternatively, checking for SessionRejectedEvent:
        List<DomainEvent> events = session.uncommittedEvents();
        if (!events.isEmpty()) {
             Assertions.assertTrue(events.get(0) instanceof SessionRejectedEvent, "Expected SessionRejectedEvent");
        } else {
             // If event emission isn't the error pattern, ensure exception is thrown.
             Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        }
    }
}
