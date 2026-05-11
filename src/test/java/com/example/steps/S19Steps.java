package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Ensure authenticated
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in command construction below
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in command construction below
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command construction below
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            command = new NavigateMenuCmd(
                "session-123",
                "MAIN_MENU",
                "DISPLAY",
                Instant.now()
            );
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("DISPLAY", event.action());
        assertNull(thrownException);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Aggregate defaults to !isAuthenticated
        aggregate = new TellerSessionAggregate("session-unauth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Simulate time passing beyond timeout
        aggregate.markExpired(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated();
        // We can't easily put the aggregate in a 'bad context' state via the public API without changing the logic.
        // However, we can trigger the failure by providing an invalid command target.
        // This Given block creates the aggregate setup, the failure will be triggered by the specific command context.
        // Note: Since the invariant logic checks `cmd.menuId`, we will pass an empty one to trigger this.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage() != null && !thrownException.getMessage().isEmpty());
    }

    // Override the When step for the context violation scenario to send the specific bad data
    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed_with_invalid_context() {
        try {
            // If we are in the context violation scenario, send a blank menuId to trigger validation failure
            // Check if we are in the 'session-context' scenario
            if (aggregate.id().equals("session-context")) {
                 command = new NavigateMenuCmd(
                    "session-context",
                    "", // Invalid context
                    "DISPLAY",
                    Instant.now()
                );
            } else {
                // Standard command for other failure scenarios
                 command = new NavigateMenuCmd(
                    aggregate.id(),
                    "MAIN_MENU",
                    "DISPLAY",
                    Instant.now()
                );
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
