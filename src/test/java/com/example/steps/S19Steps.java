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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Scenario 1: Success ---
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivityAt(Instant.now()); // Ensure it's fresh
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization
        assertNotNull(aggregate.getSessionId());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Context: Will be provided in the 'When' step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context: Will be provided in the 'When' step
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Scenario 2: Auth Failure ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth", Duration.ofMinutes(15));
        // Intentionally NOT calling markAuthenticated()
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_unauth() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-unauth", "MAIN_MENU", "ENTER");
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check for specific invariant message or generic IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Scenario 3: Timeout ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_is_timed_out() {
        aggregate = new TellerSessionAggregate("session-timeout", Duration.ofMinutes(15));
        aggregate.markAuthenticated("teller-002");
        // Set last activity to 20 minutes ago (past the 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_timeout() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain_error_timeout") {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("timeout"));
    }

    // --- Scenario 4: Invalid State/Context ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_with_invalid_context() {
        aggregate = new TellerSessionAggregate("session-bad-state", Duration.ofMinutes(15));
        aggregate.markAuthenticated("teller-003");
        aggregate.setLastActivityAt(Instant.now());
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_invalid() {
        // Passing null/blank action to violate operational context validation
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-state", "MAIN_MENU", ""); 
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain_error_invalid_state") {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
