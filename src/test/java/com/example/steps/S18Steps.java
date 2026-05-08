package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // State builders for the scenarios
    private String validTellerId = "TELLER-001";
    private String validTerminalId = "TERM-3270-A";
    private boolean isAuthenticated = true;
    private long inactiveMillis = 0;
    private String navigationContext = "HOME_SCREEN";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aValidTellerSessionAggregate();
        isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aValidTellerSessionAggregate();
        // Exceeding the hardcoded limit of 900000 ms (15 mins)
        inactiveMillis = 900001;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aValidTellerSessionAggregate();
        navigationContext = "";
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled by default state builder
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled by default state builder
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                validTellerId,
                validTerminalId,
                isAuthenticated,
                inactiveMillis,
                navigationContext
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", evt.type());
        assertEquals(aggregate.id(), evt.aggregateId());
        assertEquals(validTellerId, evt.tellerId());
        assertEquals(validTerminalId, evt.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception was not thrown");
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
