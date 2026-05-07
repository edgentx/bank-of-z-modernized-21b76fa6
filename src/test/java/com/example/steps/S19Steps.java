package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
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

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup state to be valid (Authenticated, Active, Valid Context)
        aggregate.apply(new TellerSessionAuthenticatedEvent(sessionId, "teller-1", Instant.now()));
        aggregate.apply(new MenuNavigatedEvent(sessionId, "MAIN_MENU", "VIEW", Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "sess-123";
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "TX_MENU";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        Assertions.assertNull(thrownException);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // State is null/default, isAuthenticated is false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.apply(new TellerSessionAuthenticatedEvent(sessionId, "teller-1", Instant.now().minusSeconds(3600)));
        // Simulate timeout by setting last activity time far in the past
        aggregate.setLastActivityTime(Instant.now().minusSeconds(3600));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "sess-bad-nav";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.apply(new TellerSessionAuthenticatedEvent(sessionId, "teller-1", Instant.now()));
        // Aggregate is authenticated, but no prior navigation has occurred
        // Trying to go to a submenu without a parent context might be the invariant violation
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(resultEvents == null || resultEvents.isEmpty());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvidedForInvalidCase() {
        this.sessionId = aggregate.id();
    }
}
