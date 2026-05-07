package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Initialize state to valid defaults manually for test setup
        aggregate.testSetAuthenticated(true);
        aggregate.testSetLastActivity(Instant.now());
        aggregate.testSetCurrentContext("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by command initialization in 'When'
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        // Handled by command initialization in 'When'
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = "session-no-auth";
        aggregate = new TellerSessionAggregate(id);
        aggregate.testSetAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.testSetAuthenticated(true);
        aggregate.testSetLastActivity(Instant.now().minus(Duration.ofMinutes(31))); // Assuming 30 min timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String id = "session-bad-ctx";
        aggregate = new TellerSessionAggregate(id);
        aggregate.testSetAuthenticated(true);
        aggregate.testSetLastActivity(Instant.now());
        // Set current context to something that might conflict or just be 'locked'
        aggregate.testSetCurrentContext("LOCKED_STATE");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default valid command data, overridden by specific violation scenarios if necessary
            String menuId = "DEPOSIT_MENU";
            String action = "OPEN";
            
            // If context violation, we might try a navigation that is invalid for LOCKED_STATE
            if (aggregate.getCurrentContext().equals("LOCKED_STATE")) {
                 menuId = "ADMIN_MENU"; // Invalid for locked teller
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Ideally check for specific DomainException, but IllegalArgumentException/IllegalStateException are valid contract violations
        Assertions.assertTrue(
            thrownException instanceof IllegalArgumentException || 
            thrownException instanceof IllegalStateException ||
            thrownException instanceof UnknownCommandException
        );
    }
}
