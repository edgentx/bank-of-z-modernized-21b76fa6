package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate session;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSessionAggregate("SESSION-101");
        // Simulate previous login event to make session authenticated & active
        // In a real scenario, we'd execute a LoginCmd, but for setup we prime the state
        session.hydrateForTest("TELLER-1", true, Instant.now().minusSeconds(60));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in When block via command object
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in When block via command object
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-101", "MAIN_MENU", "ENTER");
        try {
            resultEvents = session.execute(cmd);
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
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        session = new TellerSessionAggregate("SESSION-401");
        // Hydrate without authentication
        session.hydrateForTest(null, false, Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSessionAggregate("SESSION-408");
        // Hydrate with a last active timestamp > 30 mins ago (configured timeout)
        session.hydrateForTest("TELLER-1", true, Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        session = new TellerSessionAggregate("SESSION-400");
        // Hydrate normally
        session.hydrateForTest("TELLER-1", true, Instant.now());
        // Manually corrupt state for test purposes (simulating context mismatch)
        session.forceNavigationStateMismatch();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for invariant violations
        assertTrue(capturedException instanceof IllegalStateException);
    }

}