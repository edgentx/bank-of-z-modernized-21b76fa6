package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.command.NavigateMenuCmd;
import com.example.domain.tellersession.event.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSession aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSession("session-123");
        this.aggregate.markAuthenticated("teller-456");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(this.aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the execution step via command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the execution step via command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            var cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSession("session-unauth");
        // Do not authenticate
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSession("session-timeout");
        this.aggregate.markAuthenticated("teller-456");
        this.aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSession("session-bad-state");
        this.aggregate.markAuthenticated("teller-456");
    }

    // Overriding the When for the negative test to inject bad data for the last scenario
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        try {
            var cmd = new NavigateMenuCmd("session-bad-state", "", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}