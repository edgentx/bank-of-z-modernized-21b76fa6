package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Shared In-Memory Repository Mock (simulating loader)
    private TellerSessionAggregate loadAggregate(String id) {
        return new TellerSessionAggregate(id);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = loadAggregate(id);
        this.currentSessionId = id;
        // Simulate authenticated state and active timeout
        this.aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller123", Instant.now()));
        this.aggregate.applyHistory(new TellerSessionActivatedEvent(id, Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        this.aggregate = loadAggregate(id);
        this.currentSessionId = id;
        // Intentionally do NOT apply authentication event
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        this.aggregate = loadAggregate(id);
        this.currentSessionId = id;
        // Simulate authenticated state but timed out
        this.aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller123", Instant.now().minusSeconds(3600))); // Old event
        this.aggregate.applyHistory(new TellerSessionTimedOutEvent(id, Instant.now())); // Explicitly timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String id = UUID.randomUUID().toString();
        this.aggregate = loadAggregate(id);
        this.currentSessionId = id;
        // Authenticated, but navigation is locked or invalid
        this.aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller123", Instant.now()));
        this.aggregate.applyHistory(new TellerSessionLockedEvent(id, "Context violation", Instant.now()));
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the setup steps
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.currentMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.currentAction = "OPEN";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(currentMenuId, event.targetMenuId());
        Assertions.assertEquals(currentAction, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(thrownException instanceof IllegalStateException || 
                              thrownException instanceof IllegalArgumentException ||
                              thrownException instanceof UnknownCommandException);
    }
}
