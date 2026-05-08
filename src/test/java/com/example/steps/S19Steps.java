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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helpers
    private void setupValidSession() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated
        aggregate.setCurrentMenu("MAIN_MENU"); // Ensure state
        // Activity is recent by default in constructor
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        setupValidSession();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Logic handled in execute context
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Logic handled in execute context
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Standard valid execution params
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "TRANSACTIONS", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
            if (resultEvents != null && !resultEvents.isEmpty()) {
                repository.save(aggregate);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // Rejection Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Note: aggregate.markAuthenticated() is NOT called. isAuthenticated defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.expireSession(); // Helper to set lastActivityAt too far in past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("CURRENT_MENU"); // Set current state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In DDD, breaking invariants is usually an IllegalStateException or a specific Domain Exception
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException);
    }

    // Context specific overrides for the rejection scenarios to trigger the specific violation path
    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed_rejection() {
        try {
            NavigateMenuCmd cmd;
            
            // Detect context based on aggregate state to target specific invariants
            if (!aggregate.isAuthenticated()) {
                // Auth violation
                cmd = new NavigateMenuCmd(aggregate.id(), "ANY", "ACTION");
            } else if (aggregate.getCurrentMenu() != null && aggregate.getCurrentMenu().equals("CURRENT_MENU")) {
                // Context violation: try to go to the same menu without 'refresh'
                cmd = new NavigateMenuCmd(aggregate.id(), "CURRENT_MENU", "ENTER");
            } else {
                // Timeout or generic
                cmd = new NavigateMenuCmd(aggregate.id(), "ANY", "ACTION");
            }

            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Visibility into internal state for testing (in real world, exposed via getter)
    // We assume these getters exist or we add them to the aggregate.
    // The aggregate class in the generated code has getters.
}