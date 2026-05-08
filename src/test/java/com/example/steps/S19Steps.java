package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "ts-session-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate a session that has been started successfully (authenticated)
        aggregate.applyHistory(new com.example.domain.tellersession.model.TellerSessionInitializedEvent(
            this.sessionId, "teller-001", Instant.now().minusSeconds(60)
        ));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID initialized in previous step
        assertNotNull(this.sessionId);
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
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "ts-unauthenticated";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Create a session that is NOT authenticated
        // (e.g., created but not logged in, or explicitly logged out)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "ts-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Initialize session with a timestamp way in the past
        aggregate.applyHistory(new com.example.domain.tellersession.model.TellerSessionInitializedEvent(
            this.sessionId, "teller-001", Instant.now().minus(Duration.ofHours(2))
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.sessionId = "ts-invalid-context";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.applyHistory(new com.example.domain.tellersession.model.TellerSessionInitializedEvent(
            this.sessionId, "teller-001", Instant.now()
        ));
        // We rely on the command input being invalid to trigger this context error
        // Or the aggregate state being locked. For this test, we will pass an invalid 'action' or 'menuId'
        // in the subsequent steps logic (simulated here by setting class state)
        this.action = "INVALID_ACTION_FOR_CONTEXT";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}