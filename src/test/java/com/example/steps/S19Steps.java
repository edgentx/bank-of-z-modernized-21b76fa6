package com.example.steps;

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
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setTellerId("teller-01");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_is_timed_out() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        // Simulate activity 20 minutes ago with a 15 min timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.configureSessionTimeout(Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_in_restricted_mode() {
        aggregate = new TellerSessionAggregate("session-locked");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Force aggregate into a restricted operational context
        aggregate.setOperationalContext("RECONCILIATION_MODE");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly in command construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled implicitly in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled implicitly in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // For the 'Navigation state' violation test, we try to go to a menu that violates the context
            String targetMenu = "RECONCILIATION_MODE".equals(aggregate.getOperationalContext()) 
                    ? "WITHDRAWAL_MENU" // Invalid for context
                    : "MAIN_MENU";      // Valid

            command = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException carrying the invariant message
        assertTrue(caughtException instanceof IllegalStateException);
    }
}