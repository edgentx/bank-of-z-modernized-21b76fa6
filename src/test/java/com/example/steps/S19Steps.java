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
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = UUID.randomUUID().toString();
        // Constructor initializes state to valid defaults (authenticated, active)
        this.aggregate = new TellerSessionAggregate(sessionId, Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = UUID.randomUUID().toString();
        // We use the constructor that allows forcing state, or we model the aggregate
        // to start unauthenticated. Assuming the Aggregate constructor allows setting auth state.
        // For this stub, we simulate the aggregate's internal state being unauthenticated.
        this.aggregate = new TellerSessionAggregate(sessionId, false, Instant.now().plusSeconds(300));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = UUID.randomUUID().toString();
        // Create a session with a last activity time far in the past
        Instant pastActivity = Instant.now().minus(Duration.ofHours(2));
        this.aggregate = new TellerSessionAggregate(sessionId, true, pastActivity);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = UUID.randomUUID().toString();
        // Simulate a locked or invalid state context where navigation is blocked
        this.aggregate = new TellerSessionAggregate(sessionId, true, Instant.now());
        // Explicitly mark as 'LOCKED' or invalid context to trigger the specific invariant error
        this.aggregate.lockContext(); 
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the setup in 'Given' clauses, ensuring the ID is set
        Assertions.assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "MENU_MAIN_01";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(sessionId, event.aggregateId());
        Assertions.assertEquals(menuId, event.targetMenuId());
        Assertions.assertEquals(action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect a specific error type (e.g., IllegalStateException) or message
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
