package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.MenuNavigatedEvent;
import com.example.domain.tellsession.model.TellerSessionAggregate;
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
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = UUID.randomUUID().toString();
        // Setup valid state: Authenticated, Active, and Valid Context
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate authentication and initialization via internal state setters for testing purposes
        // In a real flow, this would be loaded from events.
        this.aggregate.markAuthenticated();
        this.aggregate.updateLastActivity(Instant.now());
        this.aggregate.setContextId("VALID_CONTEXT");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is initialized in the aggregate setup
        Assertions.assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Do NOT mark as authenticated.
        this.aggregate.updateLastActivity(Instant.now());
        this.aggregate.setContextId("VALID_CONTEXT");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        // Set last activity to 31 minutes ago (Timeout is 30)
        this.aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
        this.aggregate.setContextId("VALID_CONTEXT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.updateLastActivity(Instant.now());
        // Do NOT set a valid context, or set to a conflicting state
        // The constructor defaults to null, which indicates an uninitialized context.
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(this.resultEvents);
        Assertions.assertEquals(1, this.resultEvents.size());
        Assertions.assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(this.menuId, event.menuId());
        Assertions.assertEquals(this.action, event.action());
        Assertions.assertNull(this.thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(this.thrownException);
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            this.thrownException instanceof IllegalStateException || 
            this.thrownException instanceof IllegalArgumentException
        );
    }
}
