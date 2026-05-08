package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Manually configuring the aggregate to a valid state
        // In a real app, this might be done via a constructor or past events
        this.aggregate.markAuthenticated("teller-001"); 
        this.aggregate.updateLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_not_authenticated() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Leaving isAuthenticated false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_timed_out() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated("teller-001");
        // Set last activity to 2 hours ago (beyond standard 30 min timeout)
        this.aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_invalid_state() {
        this.sessionId = "session-bad-state";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated("teller-001");
        this.aggregate.updateLastActivity(Instant.now());
        // Force the aggregate into a state where it thinks it's at a screen
        // that doesn't allow the action we are about to perform (simulated via setup)
        this.aggregate.forceCurrentScreen("LOCKED_SCREEN");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is set in the @Given blocks
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

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(this.capturedException, "Should not have thrown exception: " + this.capturedException);
        Assertions.assertNotNull(this.resultEvents);
        Assertions.assertEquals(1, this.resultEvents.size());
        Assertions.assertEquals("menu.navigated", this.resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(this.capturedException);
        // Checking for specific domain exceptions (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            this.capturedException instanceof IllegalStateException || 
            this.capturedException instanceof IllegalArgumentException,
            "Expected domain violation, got: " + this.capturedException.getClass().getSimpleName()
        );
    }
}
