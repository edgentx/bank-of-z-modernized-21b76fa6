package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for S-19: NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Scenario: Successfully execute NavigateMenuCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        aggregate.markAuthenticated(); // Ensure valid state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate creation in 'a_valid_TellerSession_aggregate'
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Implicitly provided in the next step (When)
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicitly provided in the next step (When)
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Execute navigation to a valid menu state (e.g., ACCOUNT_SUMMARY)
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNT_SUMMARY", "VIEW");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_SUMMARY", event.menuId());
    }

    // Scenario: Authentication Violation

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        // Intentionally do NOT call markAuthenticated()
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario: Timeout Violation

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.markAuthenticated(); // Valid auth
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivity(Instant.now().minusSeconds(1200));
        repository.save(aggregate);
    }

    // Reuse When/Then from above (Cucumber matches by phrase)

    // Scenario: Navigation Context Violation

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-CONTEXT");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("MAIN_MENU"); // We are at Main
        repository.save(aggregate);
        // Note: The logic in TellerSessionAggregate.isValidTransition enforces that
        // you cannot go to ACTION_DEPOSIT directly from MAIN_MENU (you need to be in ACCOUNT_SUMMARY first).
    }
    
    // We need a specific When for this scenario to target the invalid transition
    @When("the NavigateMenuCmd command is executed on invalid context")
    public void the_NavigateMenuCmd_command_is_executed_invalid_context() {
        try {
            // Attempting an invalid jump based on aggregate logic
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACTION_DEPOSIT", "EXECUTE");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}