package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the aggregate construction in the previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step via Command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step via Command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.markUnauthenticated(); // Violate invariant
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        // Set activity to 20 minutes ago (Threshold is usually 15m)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated();
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default to valid data for the 'Success' scenario
            String menuId = "MAIN_MENU";
            String action = "ENTER";

            // If the aggregate is in a specific violation state, the command data itself might not matter,
            // but for the 'Navigation State' violation, we intentionally pass bad data via the command logic.
            // However, the Gherkin says the *aggregate* violates the context. The check logic in the aggregate
            // validates the command payload. We pass valid payload here; the failure will be due to state (auth/timeout).
            // For the 'Navigation state must accurately reflect...' scenario, the aggregate logic checks the command payload.
            // We will construct a standard command. The 'violation' in the Given implies we might manipulate state or command.
            // Let's stick to a standard command and let the Aggregate state drive the failure.

            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        try {
            // For the 3rd error scenario, we explicitly pass an invalid command to trigger the context check
            Command cmd = new NavigateMenuCmd(aggregate.id(), "", ""); // Invalid state data
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException based on our implementation
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // Helper to map the specific When for the violation scenario
    // Note: Cucumber maps by regex/pattern. We overloaded the When method above.
    // To be precise, we could differentiate, but typically one When method suffices if we handle logic internally.
    // For this file, I will split them slightly to ensure the 'invalid context' triggers correctly.
}
