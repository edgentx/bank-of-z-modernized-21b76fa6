package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        currentSessionId = "session-123";
        currentMenuId = "MAIN_MENU";
        currentAction = "OPEN";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Simulate an active, authenticated session
        aggregate.applyStateForTest(true, Instant.now().plusSeconds(300), "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in setup
        assertNotNull(currentSessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in setup
        assertNotNull(currentMenuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in setup
        assertNotNull(currentAction);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(currentSessionId, event.aggregateId());
        assertEquals(currentMenuId, event.menuId());
        assertEquals("menu.navigated", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        currentSessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // isAuthenticated = false
        aggregate.applyStateForTest(false, Instant.now().plusSeconds(300), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        currentSessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // lastActivityTimestamp is in the past
        aggregate.applyStateForTest(true, Instant.now().minusSeconds(3600), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        currentSessionId = "session-invalid-state";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Simulate state where navigation is locked or invalid
        aggregate.applyStateForTest(true, Instant.now().plusSeconds(300), "LOCKED_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should be thrown");
        // Validate it's an IllegalState or IllegalArgument exception
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}