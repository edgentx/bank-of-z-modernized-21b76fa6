package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Cucumber Steps for S-19: TellerSession Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate an active, authenticated session
        aggregate.hydrate("teller-001", true, "MAIN_MENU", "CICS");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Used in constructor
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "DEPOSIT_SCREEN";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Leave unauthenticated
        aggregate.hydrate(null, false, null, "NONE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.hydrate("teller-001", true, "MAIN_MENU", "CICS");
        // Force timeout
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        this.sessionId = "session-bad-ctx";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.hydrate("teller-001", true, "MAIN_MENU", "INVALID_CTX");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException for domain invariants, or UnknownCommandException
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException ||
                   caughtException instanceof UnknownCommandException);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvidedForNegative() {
        this.menuId = "TARGET_SCREEN";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvidedForNegative() {
        this.action = "F3";
    }
}
