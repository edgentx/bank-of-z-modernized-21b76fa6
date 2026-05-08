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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01"); // Make it valid per invariants
        aggregate.setCurrentMenu("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally do not call markAuthenticated
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-01");
        // Set last activity to 31 minutes ago (default threshold is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_context() {
        aggregate = new TellerSessionAggregate("session-nav-err");
        aggregate.markAuthenticated("teller-01");
        aggregate.setCurrentMenu("DASHBOARD");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled implicitly by the aggregate setup
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Determine inputs based on context setup
            String targetMenu = "ACCOUNT_DETAILS";
            String action = "ENTER";

            // If testing the context violation, we try to go to the same menu we are on
            if ("session-nav-err".equals(aggregate.id())) {
                targetMenu = aggregate.getCurrentMenu(); // Forces state violation
                action = "FWD";
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent evt = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", evt.type());
        assertEquals(aggregate.id(), evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}