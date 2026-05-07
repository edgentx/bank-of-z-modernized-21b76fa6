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

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private DomainEvent resultEvent;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup valid state
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate construction in previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Will be provided in the command execution step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be provided in the command execution step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Using standard menu/action for happy path
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertTrue(resultEvent instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        assertEquals("menu.navigated", resultEvent.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.markUnauthenticated(); // Ensure state is invalid
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); // Valid auth, but invalid timing
        aggregate.expireSession(); // Force expiration
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.markAuthenticated(); // Valid auth
        aggregate.invalidateContext(); // Invalid context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
