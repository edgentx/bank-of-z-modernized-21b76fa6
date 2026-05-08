package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private Iterable<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Ensure authenticated for base success case
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate constructor in Given
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "MAIN_MENU", "OPEN");
        resultEvents = aggregate.execute(cmd);
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        assertEquals("menu.navigated", event.type());
        assertTrue(event instanceof MenuNavigatedEvent);
    }

    // Scenario 2: Auth Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Intentionally NOT calling markAuthenticated()
    }

    @When("the NavigateMenuCmd command is executed on auth violation")
    public void the_NavigateMenuCmd_command_is_executed_auth_violation() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-auth-fail", "MAIN_MENU", "OPEN");
        try {
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error about authentication")
    public void the_command_is_rejected_with_a_domain_error_auth() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("A teller must be authenticated"));
    }

    // Scenario 3: Timeout Failure
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.setTimedOut(); // Set time into the past
    }

    @When("the NavigateMenuCmd command is executed on timeout violation")
    public void the_NavigateMenuCmd_command_is_executed_timeout_violation() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "OPEN");
        try {
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error about timeout")
    public void the_command_is_rejected_with_a_domain_error_timeout() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("Sessions must timeout"));
    }

    // Scenario 4: State Context Failure
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_state_context() {
        aggregate = new TellerSessionAggregate("session-state");
        aggregate.markAuthenticated();
        aggregate.setInvalidStateContext(); // Lock state
    }

    @When("the NavigateMenuCmd command is executed on state violation")
    public void the_NavigateMenuCmd_command_is_executed_state_violation() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-state", "MAIN_MENU", "OPEN");
        try {
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error about state")
    public void the_command_is_rejected_with_a_domain_error_state() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("Navigation state must accurately reflect"));
    }
}
