package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
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

    // Helper to create a standard valid aggregate for the success case
    private TellerSessionAggregate createValidAggregate() {
        // Assuming a constructor or factory that sets up a valid state
        // For the sake of this test, we assume a default constructor and manual state setting
        // or that the Aggregate allows setting state for testing purposes.
        // Ideally, we would use a factory method, but we will assume direct instantiation here.
        // We need to simulate a valid state: Authenticated, Not Timed out, Correct Context.
        return new TellerSessionAggregate("SESSION-1");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidAggregate();
        // Programmatically set valid state for the test
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setOperationalContext("TX_SESSION"); // Valid context
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in aggregate construction or command creation
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in command creation
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command creation
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = createValidAggregate();
        aggregate.setAuthenticated(false);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setOperationalContext("TX_SESSION");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = createValidAggregate();
        aggregate.setAuthenticated(true);
        // Set last activity to 2 hours ago (assuming timeout is 30 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setOperationalContext("TX_SESSION");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = createValidAggregate();
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Set a context that doesn't match the navigation target
        aggregate.setOperationalContext("UNKNOWN_CONTEXT");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Inputs for the command
            String menuId = "MAIN_MENU";
            String action = "SELECT";
            String targetContext = "TX_SESSION"; // Must match aggregate context for success

            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action, targetContext);
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check it's an illegal state or argument exception
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
