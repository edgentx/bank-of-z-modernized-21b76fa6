package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermenu.model.InitiateSessionCmd;
import com.example.domain.tellermenu.model.MenuNavigatedEvent;
import com.example.domain.tellermenu.model.NavigateMenuCmd;
import com.example.domain.tellermenu.model.TellerSessionAggregate;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        InitiateSessionCmd initCmd = new InitiateSessionCmd("SESSION-1", "TELLER-1", "MAIN_MENU");
        aggregate.execute(initCmd);
        aggregate.clearEvents();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate initialization in the previous step
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Context: command construction in the 'When' step
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context: command construction in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        // Aggregate starts in unauthenticated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_timed_out() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Simulate a session that was created a long time ago and never used
        InitiateSessionCmd initCmd = new InitiateSessionCmd("SESSION-TIMEOUT", "TELLER-1", "MAIN_MENU");
        aggregate.execute(initCmd);
        // Force override the last access time using reflection or accessible method if available
        // For BDD mock purposes, we assume the aggregate allows setting this state or we simulate the passage of time
        // Here we'll use a specific command or setter if the domain model supports it, or we simulate the timeout condition.
        // Since the TellerSessionAggregate above is simple, let's assume we can create one directly in a bad state via a constructor
        // or we update the state. Given the encapsulation, let's create a new one that mimics a timeout.
        // The simplest way is to construct it such that it looks timed out.
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT") {
            @Override
            public boolean isTimedOut() {
                return true;
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_invalid_context() {
        aggregate = new TellerSessionAggregate("SESSION-CONTEXT");
        InitiateSessionCmd initCmd = new InitiateSessionCmd("SESSION-CONTEXT", "TELLER-1", "MAIN_MENU");
        aggregate.execute(initCmd);
        // Force the current menu to something invalid if possible, or simulate the violation
        aggregate = new TellerSessionAggregate("SESSION-CONTEXT") {
            @Override
            public boolean isValidContext(String targetMenu) {
                return false;
            }
        };
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        caughtException = null;
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNTS_MENU", "SELECT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACCOUNTS_MENU", event.menuId());
        Assertions.assertEquals("SELECT", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
