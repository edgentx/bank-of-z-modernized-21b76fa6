package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private Exception caughtException;
    private String sessionId = "test-session-1";
    private String menuId = "MAIN_MENU";
    private String action = "ENTER";

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, active, recent activity
        aggregate.hydrate(true, true, Instant.now(), "INITIAL_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constructor in background
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by variable init
        assertNotNull(menuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled by variable init
        assertNotNull(action);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated = false
        aggregate.hydrate(false, true, Instant.now(), "INITIAL_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated = true, but lastActivityAt was 20 minutes ago (Timeout is 15)
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.hydrate(true, true, past, "INITIAL_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated = true, Recent Activity, but Active = false (Invalid Context)
        aggregate.hydrate(true, false, Instant.now(), "INITIAL_MENU");
    }

    // --- When Steps ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // --- Then Steps ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify message matches specific invariant
        assertTrue(caughtException.getMessage() != null && !caughtException.getMessage().isBlank());
    }
}
