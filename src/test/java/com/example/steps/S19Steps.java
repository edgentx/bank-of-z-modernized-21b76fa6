package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, valid context, recent activity
        aggregate.hydrate(true, "TILL_OPEN", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by aggregate creation in "Given a valid TellerSession aggregate"
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in the command execution
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in the command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: authenticated = false
        aggregate.hydrate(false, "TILL_OPEN", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: last activity was 20 mins ago (timeout is 15)
        aggregate.hydrate(true, "TILL_OPEN", Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String sessionId = "session-context-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Context is LOCKED, trying to navigate to DEPOSIT_MENU
        aggregate.hydrate(true, "LOCKED", Instant.now());
    }

    @When("the NavigateMenuCmd command is executed")
    public void executeFailingNavigateMenuCmd() {
        try {
            // Attempt to navigate to a menu that is incompatible with the current context
            String targetMenu = "DEPOSIT_MENU"; 
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // The specific message text matches the invariant check in the aggregate
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
    }
}
