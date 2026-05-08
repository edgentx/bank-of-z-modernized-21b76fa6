package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        // Create a valid, authenticated, active session
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate login
        aggregate.initializeSession("teller123");
        aggregate.updateLastActivity(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the first step setup
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the first step setup
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the first step setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Do NOT authenticate - aggregate remains in unauthenticated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.initializeSession("teller123");
        // Set last activity to 30 minutes ago (timeout is usually 15-20 mins)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(30)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "INVALID_MENU_ID";
        this.action = "ENTER";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.initializeSession("teller123");
        aggregate.updateLastActivity(Instant.now());
        // Context violation: Trying to enter a screen that requires a transaction context when none exists,
        // or simply a malformed state. The aggregate logic will enforce state consistency.
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(IllegalStateException.class.isInstance(caughtException) || IllegalArgumentException.class.isInstance(caughtException));
    }
}
