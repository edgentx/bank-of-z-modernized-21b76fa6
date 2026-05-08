package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "TELLER_001";
    private String validTerminalId = "TERM_A01";
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION_001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("SESSION_002");
        // Simulate unauthenticated state
        this.aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("SESSION_003");
        // Simulate timed out state
        this.aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("SESSION_004");
        // Simulate bad navigation state
        this.aggregate.invalidateNavigationState();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup handled in variable
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup handled in variable
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new StartSessionCmd("SESSION_001", validTellerId, validTerminalId);
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Expected an event to be emitted");
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session.started", sse.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
            "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }
}
