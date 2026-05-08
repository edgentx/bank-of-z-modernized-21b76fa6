package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Initiate session to make it valid/authenticated
        aggregate.execute(new InitiateSessionCmd("SESSION-1", "TELLER-1", "TERMINAL-1"));
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        // Aggregate created but not initiated, so isAuthenticated is false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Initiate but force lastActionTime far in the past
        aggregate.execute(new InitiateSessionCmd("SESSION-TIMEOUT", "TELLER-1", "TERMINAL-1"));
        aggregate.forceTimeoutForTesting();
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION-BADSTATE");
        aggregate.execute(new InitiateSessionCmd("SESSION-BADSTATE", "TELLER-1", "TERMINAL-1"));
        aggregate.clearEvents();
        // Force state out of sync
        aggregate.forceBadContextForTesting("LOCKED");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by 'a valid TellerSession aggregate'
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by command construction in When step
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        // Handled by command construction in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
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
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Test Runner for JUnit 5 + Cucumber
    @RunWith(JUnitPlatform.class)
    @SelectClasspathResource("features")
    @Plugin(name = "pretty")
    public static class S19TestSuite {}
}
