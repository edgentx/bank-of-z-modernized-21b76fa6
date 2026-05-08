package com.example.steps;

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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it is authenticated to be valid for the 'happy path' context
        aggregate.markAuthenticated("teller-001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        aggregate.markExpired(); // Manually set the timestamp to the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        String sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        // The violation is simulated by the command input in the 'When' step, 
        // but we can prepare the aggregate if there is internal state drift.
        aggregate.setInvalidNavigationState();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by the aggregate creation in the Given steps
        // No-op for step definition clarity
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context will be provided in the When step via the Command object
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context will be provided in the When step via the Command object
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Scenario 1: Happy Path
        if (aggregate.getCurrentMenu() == null && capturedException == null && aggregate.getClass().getSimpleName().equals("TellerSessionAggregate")) {
             // Heuristic: if we are in the happy path state (valid), execute valid command
             // We check state or use a test flag. Here we assume valid inputs.
             try {
                 NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
                 resultEvents = aggregate.execute(cmd);
             } catch (Exception e) {
                 capturedException = e;
             }
        } 
        // Scenario 4: Invalid Context (Navigation state must reflect context)
        // We verify this scenario by providing invalid inputs to the command
        else if (aggregate.getClass().getSimpleName().equals("TellerSessionAggregate")) {
             // This covers the "Violates Navigation Context" scenario 
             // because we send invalid Action/MenuId in the command
             try {
                 // Send invalid command to trigger the context error
                 NavigateMenuCmd cmd = new NavigateMenuCmd("session-context", "", "");
                 resultEvents = aggregate.execute(cmd);
             } catch (Exception e) {
                 capturedException = e;
             }
        }
        // Default generic execution used by specific violation scenarios (Auth, Timeout)
        else if (capturedException == null) {
             try {
                 // Execute a command that should fail due to Aggregate state (Auth/Timeout)
                 // The inputs themselves might be valid, but the aggregate rejects them.
                 NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
                 resultEvents = aggregate.execute(cmd);
             } catch (Exception e) {
                 capturedException = e;
             }
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect IllegalStateException or IllegalArgumentException, both indicating domain rule violations
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        System.out.println("Expected error caught: " + capturedException.getMessage());
    }
}
