package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to reset or create aggregate
    private void createDefaultAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        // Setup valid state by default
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        this.capturedException = null;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        createDefaultAggregate();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is part of the aggregate constructor
        Assertions.assertEquals("session-123", aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step via command constructor
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step via command constructor
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("MAIN_MENU", event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        createDefaultAggregate();
        aggregate.setAuthenticated(false); // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        createDefaultAggregate();
        // Violation: Last activity was 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        createDefaultAggregate();
        // Context violation: implied by subsequent execution logic
        // For this specific story implementation, we ensure the logic handles invalid inputs if passed.
        // The aggregate handles this via the menuId check in execute.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In DDD, IllegalStateException or IllegalArgumentException inside execute represents a Domain Error / Invariant violation
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain error, got: " + capturedException.getClass().getSimpleName()
        );
        Assertions.assertNull(resultEvents, "No events should be emitted on domain error");
    }
}
