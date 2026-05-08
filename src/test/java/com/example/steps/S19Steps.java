package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456"); // Ensure authenticated for happy path
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-violation-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-violation-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456");
        aggregate.markTimedOut(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        String sessionId = "session-violation-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456");
        // We need to simulate the internal state check failing.
        // Since TellerSessionAggregate.isValidNavigation relies on logic,
        // we can't directly mock it without a reflection hack or a test subclass.
        // However, if we look at the Aggregate implementation, we can rely on the logic.
        // If the logic checks `isActive`, we can set that. If not, we must trust the aggregate logic.
        // For this specific BDD, let's assume we need to feed it a command that causes the context error.
        // The simplest way given the constraints is to subclass or expose a method to force invalid state.
        aggregate.markNavigationContextInvalid(); // Using the test hook method defined in the aggregate
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization in previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Used in command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Used in command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // For the context violation scenario, we might need to send a specific command
            // or rely on the internal state set in the Given step.
            // Based on the implementation `markNavigationContextInvalid`,
            // the aggregate logic needs to pick up on this.
            // We will send a standard command.
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException);
        System.out.println("Caught expected domain error: " + thrownException.getMessage());
    }
}
