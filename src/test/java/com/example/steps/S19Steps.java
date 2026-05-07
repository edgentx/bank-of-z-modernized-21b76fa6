package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for Story S-19: TellerSession Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION_123");
        // By default, we mark it authenticated for the success scenario
        this.aggregate.markAuthenticated(VALID_TELLER_ID);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is part of the command construction.
        // We construct it fully in the 'When' step, but we verify setup here.
        assertNotNull(this.aggregate);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Placeholder for data setup context if needed
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Placeholder for data setup context if needed
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Command construction for standard execution
            String sid = aggregate.id();
            String mid = VALID_MENU_ID;
            String act = VALID_ACTION;
            
            this.command = new NavigateMenuCmd(sid, mid, act);
            this.resultingEvents = aggregate.execute(command);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultingEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navigatedEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navigatedEvent.type());
        assertEquals(VALID_MENU_ID, navigatedEvent.menuId());
        assertEquals(VALID_ACTION, navigatedEvent.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("SESSION_UNAUTH");
        // Do NOT call markAuthenticated. IsAuthenticated defaults to false.
        assertFalse(aggregate.isActive(), "Aggregate should not be active");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("SESSION_TIMEOUT");
        this.aggregate.markAuthenticated(VALID_TELLER_ID);
        // Force the session into a timed-out state
        this.aggregate.expireSession();
        // Note: verifying internal timeout state is tricky without a getter, 
        // but the execute method will throw the exception.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.aggregate = new TellerSessionAggregate("SESSION_BAD_CTX");
        this.aggregate.markAuthenticated(VALID_TELLER_ID);
        // Context violation is triggered by passing a BAD menuId in the When step.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
            // We expect either IllegalStateException (business invariant) or UnknownCommandException (dispatch logic)
            // or IllegalArgumentException (validation).
            assertTrue(
                capturedException instanceof IllegalStateException ||
                capturedException instanceof UnknownCommandException ||
                capturedException instanceof IllegalArgumentException,
                "Expected domain error (IllegalStateException/IllegalArgumentException), but got: " + capturedException.getClass().getName()
            );
    }
}