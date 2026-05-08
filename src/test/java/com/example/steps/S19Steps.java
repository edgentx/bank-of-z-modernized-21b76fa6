package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        providedSessionId = "TS-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        providedSessionId = "TS-INVALID-AUTH";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setAuthenticated(false); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        providedSessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago to violate default 15 min timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context_accuracy() {
        providedSessionId = "TS-LOCKED";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Lock the aggregate to simulate a context mismatch (e.g., transaction in progress)
        aggregate.setLocked(true);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Normally bound to the aggregate, assumed valid from context
        assertNotNull(providedSessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        providedMenuId = "MAIN_MENU_01";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        providedAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent);
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(providedMenuId, navEvent.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // The message of the exception describes the invariant violation
        assertTrue(thrownException.getMessage().length() > 0);
    }
}
