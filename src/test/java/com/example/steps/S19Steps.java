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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Setup valid state (Authenticated, Active, Valid Context)
        aggregate.apply(new TellerSessionAuthenticatedEvent(this.sessionId, "teller_01", Instant.now()));
        aggregate.apply(new TellerSessionStartedEvent(this.sessionId, Instant.now()));
        // Ensure context is valid
        aggregate.apply(new MenuNavigatedEvent(this.sessionId, "MAIN_MENU", "VIEW", Instant.now()));
        this.menuId = "ACCOUNT_MENU";
        this.action = "SELECT";
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is set in the aggregate constructor
        Assertions.assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // menuId is set in the @Given
        Assertions.assertNotNull(this.menuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // action is set in the @Given
        Assertions.assertNotNull(this.action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "TS-UNAUTH";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // DO NOT authenticate. Session is created but NOT authenticated.
        this.menuId = "ACCOUNT_MENU";
        this.action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Authenticate
        aggregate.apply(new TellerSessionAuthenticatedEvent(this.sessionId, "teller_02", Instant.now().minus(Duration.ofHours(2))));
        // Start session long ago
        aggregate.apply(new TellerSessionStartedEvent(this.sessionId, Instant.now().minus(Duration.ofHours(1))));
        
        this.menuId = "ACCOUNT_MENU";
        this.action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.sessionId = "TS-BADCTX";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Authenticate and Start
        aggregate.apply(new TellerSessionAuthenticatedEvent(this.sessionId, "teller_03", Instant.now()));
        aggregate.apply(new TellerSessionStartedEvent(this.sessionId, Instant.now()));
        // Simulate bad state: trying to navigate to 'WITHDRAWAL' without being in 'ACCOUNT_MENU'
        this.menuId = "WITHDRAWAL_SCREEN"; 
        this.action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it is an IllegalStateException or IllegalArgumentException (domain error)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof UnknownCommandException ||
            capturedException instanceof IllegalArgumentException
        );
    }
}
