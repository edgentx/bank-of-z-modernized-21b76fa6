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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // ----------------------------------------------------------------
    // Givens
    // ----------------------------------------------------------------

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup for success: Authenticated and active
        aggregate.hydrate(true, "MAIN_MENU", 1); // 1 min ago (active)
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly in command construction, or explicit check
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context setup, nothing to do until command is built
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Set authenticated = false
        aggregate.hydrate(false, "LOGIN_SCREEN", 1);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Authenticated, but last activity was 20 mins ago (Timeout is 15)
        aggregate.hydrate(true, "MAIN_MENU", 20);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        // Authenticated, Active, but Current State is LOCKED
        aggregate.hydrate(true, "LOCKED", 1);
    }

    // ----------------------------------------------------------------
    // Whens
    // ----------------------------------------------------------------

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            cmd = new NavigateMenuCmd(aggregate.id(), "CUSTOMER_DETAILS", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // ----------------------------------------------------------------
    // Thens
    // ----------------------------------------------------------------

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals("CUSTOMER_DETAILS", navEvent.menuId());
        assertEquals("session-123", navEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException,
                   "Exception should be a domain error (IllegalState or IllegalArgument)");
    }

}