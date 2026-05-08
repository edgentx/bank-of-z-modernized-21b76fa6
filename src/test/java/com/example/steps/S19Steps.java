package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
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

    private static final String SESSION_ID = "session-123";
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Initialize with valid state (Authenticated, Active)
        // This effectively bypasses the 'StartSession' command for direct testing
        aggregate.initializeForTesting(SESSION_ID, "teller-1", Instant.now().plusSeconds(1800), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // State: Not Authenticated
        aggregate.initializeForTesting(SESSION_ID, null, Instant.now().plusSeconds(1800), "LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // State: Expired (last activity time was way in the past)
        aggregate.initializeForTesting(SESSION_ID, "teller-1", Instant.now().minusSeconds(3600), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // State: Valid Auth/Time, but Current Context is LOCKED, disallowing navigation
        aggregate.initializeForTesting(SESSION_ID, "teller-1", Instant.now().plusSeconds(1800), "LOCKED_SCREEN");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is implicit in the aggregate initialization
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "ACCOUNTS_SUMMARY", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACCOUNTS_SUMMARY", event.targetMenuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception but command succeeded");
        // We verify it's a known domain error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain violation error, got: " + capturedException.getClass().getSimpleName()
        );
    }
}
