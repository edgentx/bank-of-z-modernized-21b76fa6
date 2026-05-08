package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.MenuNavigatedEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellsession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private boolean forceInvalidAction = false;

    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";
    private static final Duration TIMEOUT = Duration.ofMinutes(30);

    // 1. Valid Aggregate
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        reset();
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated("teller-1");
        repo.save(aggregate);
    }

    // 2. Unauthenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_not_authenticated() {
        reset();
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID, TIMEOUT);
        // Intentionally not authenticated
        repo.save(aggregate);
    }

    // 3. Expired
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_expired() {
        reset();
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated("teller-1");
        aggregate.expireSession();
        repo.save(aggregate);
    }

    // 4. Invalid Context (Simulated by forcing an invalid action)
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_invalid_context() {
        reset();
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID, TIMEOUT);
        aggregate.markAuthenticated("teller-1");
        forceInvalidAction = true; // Triggers blank action in command
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd;
            if (forceInvalidAction) {
                cmd = new NavigateMenuCmd(VALID_SESSION_ID, VALID_MENU_ID, ""); // Blank action
            } else {
                cmd = new NavigateMenuCmd(VALID_SESSION_ID, VALID_MENU_ID, VALID_ACTION);
            }

            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    private void reset() {
        forceInvalidAction = false;
        caughtException = null;
        resultEvents = null;
    }
}