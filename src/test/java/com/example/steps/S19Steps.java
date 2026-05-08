package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid defaults
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "session-invalid-auth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(false);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago to violate 30 min timeout
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        this.aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-invalid-nav";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
        // Current menu is set to a valid context, but we will try to navigate to an invalid one
        this.aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate initialization, ensuring ID matches
        assertNotNull(this.aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "DEPOSIT_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // If the scenario sets invalid context, we use it here
            if (aggregate.getCurrentMenuId() == null) { 
                // Violation context specific setup
                this.menuId = "UNKNOWN_MENU"; 
            }
            
            var cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
            this.thrownException = null;
        } catch (Exception e) {
            this.thrownException = e;
            this.resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(this.resultEvents);
        assertEquals(1, this.resultEvents.size());
        assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
        assertEquals(this.menuId, event.menuId());
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(this.thrownException);
        // We expect IllegalStateException for invariants or IllegalArgumentException for validation
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}