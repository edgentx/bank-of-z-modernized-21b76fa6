package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate session;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private final String sessionId = "session-123";
    private final String menuId = "MAIN_MENU";
    private final String action = "OPEN";
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSessionAggregate(sessionId, Duration.ofMinutes(30));
        // Pre-condition: Session is authenticated for success scenario
        session.markAuthenticated("teller-001");
        session.setContext("DEFAULT");
        repo.save(session);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSessionAggregate(sessionId, Duration.ofMinutes(30));
        // Intentionally do NOT mark authenticated
        repo.save(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSessionAggregate(sessionId, Duration.ofMinutes(30));
        session.markAuthenticated("teller-001");
        // Force the session to appear expired
        session.markExpired();
        repo.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        session = new TellerSessionAggregate(sessionId, Duration.ofMinutes(30));
        session.markAuthenticated("teller-001");
        // Set context to null or a mismatched context for the action
        // The validation logic checks if action is ViewDetails and context is null -> fail
        session.setContext(null);
        repo.save(session);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by constants in setup, assumed valid
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled by constants in setup, assumed valid
        assertNotNull(menuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // For the negative context test, we need an action that requires context
        // We will use "ViewDetails" in that specific scenario logic if needed,
        // but here we just assert the variable is ready.
        assertNotNull(action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // If we are in the 'context violation' scenario, override action to trigger the specific business rule
            String actionToUse = action;
            if (session.getTellerId() != null && session.getContext() == null) {
                // We are in the context violation scenario (Authenticated but no context)
                actionToUse = "ViewDetails";
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, actionToUse);
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("A teller must be authenticated") ||
                   capturedException.getMessage().contains("Sessions must timeout") ||
                   capturedException.getMessage().contains("Navigation state must accurately reflect"));
    }
}