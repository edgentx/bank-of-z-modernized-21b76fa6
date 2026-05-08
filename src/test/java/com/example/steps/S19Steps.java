package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // State setup for the happy path
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID initialized in aggregate constructor
        Assertions.assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "DEPOSIT_SCREEN";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    // Violations setup
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "SESSION-401";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.menuId = "ADMIN_SCREEN";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-408";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.menuId = "HOME";
        this.action = "REFRESH";
        // We will inject a very old timestamp in the command to simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "SESSION-409";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.menuId = "RANDOM_SUBMENU";
        this.action = "JUMP";
        // We will inject a context ID in the command that triggers the violation
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Base command properties
        long now = Instant.now().toEpochMilli();
        boolean isAuthenticated = true;
        long timeout = 900_000L; // 15 mins
        String contextId = "MAIN_MENU"; // Valid context

        // Adjust properties based on the scenario logic (inferred from 'Given' step method)
        // Since Cucumber context doesn't explicitly link Givens to Whens via IDs, we use heuristic logic on aggregate state
        if (sessionId.equals("SESSION-401")) {
            isAuthenticated = false; // Trigger Auth Violation
        } else if (sessionId.equals("SESSION-408")) {
            now = now - 1_000_000L; // Trigger Timeout Violation (older than timeout)
        } else if (sessionId.equals("SESSION-409")) {
            contextId = "INVALID_CONTEXT"; // Trigger Context Violation
        }

        NavigateMenuCmd cmd = new NavigateMenuCmd(
                sessionId,
                menuId,
                action,
                "TELLER-1",
                isAuthenticated,
                now, // last activity
                timeout,
                contextId
        );

        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
