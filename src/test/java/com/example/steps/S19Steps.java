package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private String sessionId;
    private String menuId;
    private String action;
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.menuId = "MAIN_MENU";
        this.action = "VIEW";
        
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Initialize to a valid state (Authenticated, Active)
        aggregate.apply(new SessionAuthenticatedEvent(this.sessionId, "teller-1", Instant.now()));
        aggregate.apply(new SessionActivatedEvent(this.sessionId, Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        this.sessionId = "session-123";
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "ACCOUNT_DETAILS";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Deliberately not calling apply(AuthenticatedEvent) -> defaults to unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.apply(new SessionAuthenticatedEvent(this.sessionId, "teller-1", Instant.now().minusSeconds(3600)));
        aggregate.apply(new SessionActivatedEvent(this.sessionId, Instant.now().minusSeconds(3600)));
        // Last activity was 1 hour ago, violating timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        this.sessionId = "session-bad-context";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.apply(new SessionAuthenticatedEvent(this.sessionId, "teller-1", Instant.now()));
        aggregate.apply(new SessionActivatedEvent(this.sessionId, Instant.now()));
        // Simulate a context violation by providing a command that implies an invalid transition
        // or we can set the aggregate state to a locked state.
        // For this test, we will attempt to navigate to a restricted context while state is pending.
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(menuId, event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // In a real system we might check specific error types, here we check generic failure
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}