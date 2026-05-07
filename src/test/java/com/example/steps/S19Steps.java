package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private String sessionId;
    private String menuId;
    private String action;

    // Scenario: Successfully execute NavigateMenuCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01"); // Simulate auth
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            List<DomainEvent> events = aggregate.execute(cmd);
            // Process events (load into aggregate if we had a generic apply method, 
            // but for this test we just verify the output list)
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        List<DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    // Scenario: NavigateMenuCmd rejected — A teller must be authenticated

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated()
        menuId = "PROTECTED_SCREEN";
        action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Scenario: NavigateMenuCmd rejected — Sessions must timeout

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
        menuId = "MAIN_MENU";
        action = "ENTER";
    }

    // Scenario: NavigateMenuCmd rejected — Navigation state context

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        sessionId = "session-context-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.setCurrentContext(null); // No context set
        menuId = "txn_deposit"; // Requires context
        action = "ENTER";
    }
}
