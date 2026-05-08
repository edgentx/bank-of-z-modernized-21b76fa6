package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<com.example.domain.shared.DomainEvent> events;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup state that implies validity: Authenticated, Active, Consistent context
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(Instant.now());
        aggregate.setNavigationContext("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-401");
        // Intentionally do not call markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        this.aggregate = new TellerSessionAggregate("session-409");
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(Instant.now());
        // Set context to a state that doesn't exist or is invalid for the transition
        aggregate.setNavigationContext("INVALID_STATE");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by aggregate construction in the Given steps above
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Validated by the command execution logic
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Validated by the command execution logic
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Using a valid target/context for the happy path
            String targetMenu = (aggregate.getCurrentContext() != null && aggregate.getCurrentContext().equals("MAIN_MENU")) 
                ? "DEPOSIT_MENU" : "MAIN_MENU";
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            this.events = aggregate.execute(cmd);
        } catch (IllegalStateException | DomainException | UnknownCommandException e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(events);
        Assertions.assertFalse(events.isEmpty());
        Assertions.assertEquals("menu.navigated", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // In DDD, rule violations throw exceptions (Domain Errors)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || 
                              thrownException instanceof DomainException);
    }
}