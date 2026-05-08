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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Session defaults
    private static final String VALID_SESSION_ID = "SESSION-42";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create an active, authenticated session
        this.aggregate = TellerSessionAggregate.createActive(VALID_SESSION_ID, VALID_MENU_ID);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create an unauthenticated session
        this.aggregate = TellerSessionAggregate.createUnauthenticated(VALID_SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Create a session that has timed out
        this.aggregate = TellerSessionAggregate.createTimedOut(VALID_SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Create a locked/invalid state session
        this.aggregate = TellerSessionAggregate.createLocked(VALID_SESSION_ID);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate setup context, implicit in the command execution
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Implicit in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Implicit in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, "NEW_MENU", "F3");
            this.resultEvents = aggregate.execute(cmd);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(capturedException instanceof IllegalStateException 
                   || capturedException instanceof UnknownCommandException
                   || capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException/IllegalArgumentException), got: " + capturedException.getClass().getSimpleName());
    }
}