package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
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
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in aggregate creation, but we verify it exists
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Context setup
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context setup
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // We use a valid command payload by default in this step for the positive path
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "OPEN");
            // Reload from repo to ensure we are testing persistence/hydration logic if it existed,
            // but here we act on the instance.
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // Persist state changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.setAuthenticated(false); // Explicitly not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.markAuthenticated();
        // We handle this via invalid command data in the 'When' or specific state setup.
        // For this aggregate, invalid context is triggered by invalid command inputs.
    }

    @When("the NavigateMenuCmd command is executed on invalid state")
    public void the_navigate_menu_cmd_command_is_executed_on_invalid_state() {
        try {
            // For the "Navigation state" violation, we pass invalid inputs to the command
            // which triggers the invariant check inside the aggregate.
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-state", "", ""); // Blank menuId violates invariant
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // Hooks to handle the specific "When" for the valid state scenario if overloaded
    // However, Cucumber matches regex. The text is identical. We will combine logic or differentiate regex.
    // Since "When the NavigateMenuCmd command is executed" is used in both positive and negative (timeout/auth)
    // The specific state setup in Given determines the outcome.

}
