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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to setup a fresh aggregate
    private void resetAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.capturedException = null;
        this.resultEvents = null;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        resetAggregate();
        // Simulate a valid, authenticated session state
        aggregate.markAuthenticated("teller-42");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in resetAggregate via constructor default
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Implicit in the command creation in 'When'
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicit in the command creation in 'When'
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        resetAggregate();
        // Do NOT call markAuthenticated(). It defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        resetAggregate();
        aggregate.markAuthenticated("teller-42");
        aggregate.markSessionInactive(); // Force the timestamp into the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        resetAggregate();
        aggregate.markAuthenticated("teller-42");
        // Simulate a context mismatch
        aggregate.setOperationalContext("INVALID_CONTEXT");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("MAIN_MENU", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}