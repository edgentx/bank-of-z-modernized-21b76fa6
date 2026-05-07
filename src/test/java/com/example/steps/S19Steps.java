package com.example.steps;

import com.example.domain.shared.*;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {
    private TellerSession session;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSession("session-123");
        // Simulate a prior login event to make it valid
        session.execute(new LoginTellerCmd("session-123", "teller-1", Instant.now()));
        session.clearEvents(); // Clear setup events so we only test the command
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSession("session-no-auth");
        // Do not authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSession("session-timeout");
        // Simulate a login that happened a long time ago (e.g. 30 minutes)
        session.execute(new LoginTellerCmd("session-timeout", "teller-1", Instant.now().minus(Duration.ofMinutes(30))));
        session.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        session = new TellerSession("session-state");
        session.execute(new LoginTellerCmd("session-state", "teller-1", Instant.now()));
        session.execute(new NavigateMenuCmd("session-state", "MAIN_MENU", "ENTER", Instant.now()));
        session.clearEvents();
        // Violate the rule: trying to perform an action that requires a different screen context
        // e.g. trying to POST_TRANSACTION while on MAIN_MENU
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by context setup in aggregate creation
        assertNotNull(session.id());
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
        String menuId = "DEPOSIT_MENU";
        String action = "ENTER";
        
        // If we are testing the state violation, pick a contextually invalid action for the violation scenario
        if (session.id().equals("session-state")) {
            menuId = "POST_TX_MENU"; // Trying to jump to a specific transaction processing screen from main
            action = "SUBMIT";
        }

        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), menuId, action, Instant.now());
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check for specific exception types based on invariants
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
