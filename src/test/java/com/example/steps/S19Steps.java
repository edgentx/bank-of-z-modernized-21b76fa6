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
        aggregate.markAuthenticated(); // Ensure valid state
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate creation in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be provided in the When step via context or direct string
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be provided in the When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.expireSession(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        // The violation is the *action* provided in the When step, not necessarily the aggregate state itself,
        // though the aggregate enforces the check. We set up a valid aggregate here.
        String sessionId = "session-bad-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        executeCommand("MENU_MAIN", "ENTER");
    }

    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        // This specific When method maps to the context violation scenario
        // We provide a valid menu but an invalid action (e.g., 'JUMP' instead of 'ENTER')
        executeCommand("MENU_MAIN", "INVALID_ACTION");
    }

    private void executeCommand(String menuId, String action) {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("MENU_MAIN", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Check for the specific invariant violations based on the scenario
        assertTrue(
            capturedException.getMessage().contains("authenticated") ||
            capturedException.getMessage().contains("timeout") ||
            capturedException.getMessage().contains("context") ||
            capturedException.getMessage().contains("Invalid action"),
            "Exception message did not match expected invariants: " + capturedException.getMessage()
        );
    }

    // Cucumber will automatically find the appropriate When method based on the scenario context logic,
    // but since the Gherkin text is identical, we can use a single hook or named parameters.
    // For simplicity in Java Step Definitions without parameterization complexity, we handle it via
    // specific Given states implying specific When logic if needed, or combined checks.
    // However, Cucumber matches by text. We need a method that matches the text in the feature.
    // I will map the specific "invalid context" scenario to a specific step definition injection.
    // Since the text in feature is generic, we can detect the state.

    // To strictly follow the feature file text provided:
    // "When the NavigateMenuCmd command is executed" -> theNavigateMenuCmdCommandIsExecuted()
    // This implies we might need to rely on the 'Given' state to determine the command params or
    // expect an exception. However, Java methods are unique by name + parameters.
    // I will add a specific method for the Context violation if I can alter the feature text, but I cannot.
    // So I will use a single When method and detect which scenario we are in based on the aggregate state.

    @Override // Re-defining the single When method to handle all cases intelligently
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Heuristic: If the session is valid but we want to test context error, send bad action.
        // This is a slight compromise for strict Gherkin-to-Java mapping without scenario outlines.
        if (aggregate.isAuthenticated() && !isSessionExpired(aggregate)) {
            // If auth and valid session, maybe we are testing the context error?
            // We assume the Context test sends a bad command.
             executeCommand("MENU_MAIN", "INVALID_ACTION_FOR_CONTEXT");
        } else {
            // Default command
            executeCommand("MENU_MAIN", "ENTER");
        }
    }

    private boolean isSessionExpired(TellerSessionAggregate agg) {
        // Helper to detect the timeout scenario state
        // We check if last activity is significantly old
        return agg.getLastActivityAt().isBefore(java.time.Instant.now().minusSeconds(60)); // Simplified check
    }
}
