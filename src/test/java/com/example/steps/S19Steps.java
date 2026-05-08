package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure pre-conditions for success are met (Authenticated, Active)
        aggregate.markAsAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate construction above
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command construction below
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command construction below
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    // Scenario: NavigateMenuCmd rejected — A teller must be authenticated to initiate a session.
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark as authenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception was not thrown");
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario: NavigateMenuCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated(); // Must be authenticated to pass first check
        aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        // Set last activity to 31 minutes ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    // Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect the current operational context.
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set current context to something that invalidates the target navigation
        // Logic: If Current is 'LOAN_VIEW', cannot go to 'EXIT_CONFIRM' directly (must return to MAIN)
        aggregate.setCurrentMenuId("LOAN_VIEW");
    }

    // Overriding the When/Then for negative scenarios to fit the generic validation logic
    // The When step triggers the specific command to test the violation
    
    // Helper for timeout specific navigation command
    @When("the NavigateMenuCmd command is executed for timeout scenario")
    public void executeNavForTimeout() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Helper for state context specific navigation command
    @When("the NavigateMenuCmd command is executed for invalid state context")
    public void executeNavForInvalidContext() {
        // Attempting to go to EXIT_CONFIRM from LOAN_VIEW (invalid state transition)
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-nav", "EXIT_CONFIRM", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

}
