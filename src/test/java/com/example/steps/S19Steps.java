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

    // --- Scenario: Successfully execute NavigateMenuCmd ---
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = TellerSessionAggregate.authenticated("sess-123", "teller-1");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled by the aggregate instance in this test context
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in 'When'
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in 'When'
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("sess-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    // --- Scenario: NavigateMenuCmd rejected — Authentication ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = TellerSessionAggregate.unauthenticated("sess-999");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected domain error");
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Scenario: NavigateMenuCmd rejected — Timeout ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = TellerSessionAggregate.expired("sess-timeout");
    }

    // --- Scenario: NavigateMenuCmd rejected — Context ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = TellerSessionAggregate.authenticated("sess-context", "teller-1");
    }

    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        try {
            // Passing blank menuId to simulate context violation
            NavigateMenuCmd cmd = new NavigateMenuCmd("sess-context", "", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
