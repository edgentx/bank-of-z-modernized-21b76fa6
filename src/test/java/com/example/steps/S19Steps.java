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
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01"); // Ensure authenticated
        aggregate.setCurrentMenu("MAIN_MENU");
        aggregate.setLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate initialization
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be set in cmd construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be set in cmd construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default valid command for the happy path
            String targetMenu = "ACCOUNT_DETAILS";
            if (cmd == null) {
                 cmd = new NavigateMenuCmd("session-123", targetMenu, "ENTER");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Intentionally do not call markAuthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated("teller-01");
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(java.time.Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.markAuthenticated("teller-01");
        // Set an invalid context state
        aggregate.setCurrentMenu("INOPERATIVE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
