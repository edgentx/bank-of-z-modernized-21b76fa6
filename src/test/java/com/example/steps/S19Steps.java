package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception caughtException;
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(15);

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate session start via internal reflection or public helper if available. 
        // For BDD isolation, we assume a stateful aggregate exists.
        // We prime it to be valid (authenticated, active, correct context).
        // Using reflection here to bypass non-existent InitiateCmd for this S-19 scope
        try {
            var stateField = aggregate.getClass().getDeclaredField("authenticated");
            stateField.setAccessible(true);
            stateField.setBoolean(aggregate, true);
            
            var activeField = aggregate.getClass().getDeclaredField("active");
            activeField.setAccessible(true);
            activeField.setBoolean(aggregate, true);

            var contextField = aggregate.getClass().getDeclaredField("currentMenuContext");
            contextField.setAccessible(true);
            contextField.set(aggregate, "MAIN_MENU");
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup test state", e);
        }
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "TX_MENU", "OPEN");
        try {
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
        assertEquals("TX_MENU", event.targetMenuId());
        assertEquals("OPEN", event.action());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Force state to unauthenticated
        try {
            var stateField = aggregate.getClass().getDeclaredField("authenticated");
            stateField.setAccessible(true);
            stateField.setBoolean(aggregate, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Force state to authenticated but expired
        try {
            var authField = aggregate.getClass().getDeclaredField("authenticated");
            authField.setAccessible(true);
            authField.setBoolean(aggregate, true);

            var lastAccessField = aggregate.getClass().getDeclaredField("lastAccessAt");
            lastAccessField.setAccessible(true);
            lastAccessField.set(aggregate, Instant.now().minus(SESSION_TIMEOUT).minusSeconds(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        // Force state to authenticated but invalid context (e.g. null or locked)
        try {
            var authField = aggregate.getClass().getDeclaredField("authenticated");
            authField.setAccessible(true);
            authField.setBoolean(aggregate, true);

            var contextField = aggregate.getClass().getDeclaredField("currentMenuContext");
            contextField.setAccessible(true);
            contextField.set(aggregate, "LOCKED_STATE"); // A state that doesn't allow navigation
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect either IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
