package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: TellerSession NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String testSessionId = "sess-123";
    private String testMenuId = "MAIN_MENU";
    private String testAction = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated(); // Ensure base valid state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        testSessionId = "sess-123";
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        testMenuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        testAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(testSessionId, testMenuId, testAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(testMenuId, event.menuId());
        assertEquals(testAction, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(testSessionId);
        // Do NOT authenticate. Default state is unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Force expiration
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated();
        aggregate.markContextInvalid(); // Force context lock
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // The domain throws IllegalStateException or IllegalArgumentException for domain rule violations
        assertTrue(
                capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected domain violation exception, got: " + capturedException.getClass().getSimpleName()
        );
        assertTrue(capturedException.getMessage().contains("Navigation failed"), "Exception message should indicate failure reason");
    }
}
