package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
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
    private InMemoryTellerSessionRepository repository;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // Helper to setup a valid baseline aggregate
    private void setupValidAggregate() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-123");
        // Mark authenticated so it's valid by default
        aggregate.markAuthenticated("teller-001");
        // Set a distinct initial menu
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        setupValidAggregate();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        repository = new InMemoryTellerSessionRepository();
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally do not call markAuthenticated()
        aggregate.setCurrentMenu("LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        setupValidAggregate();
        // Set last activity to 31 minutes ago (threshold is 30m)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        setupValidAggregate();
        // Set current menu to the one we will try to navigate to, causing a conflict/stale state error
        aggregate.setCurrentMenu("DEPOSIT_SCREEN");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // SessionId is implicit in the aggregate creation above, stored in aggregate.id()
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // MenuId will be used in the When clause
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Action will be used in the When clause
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Determine target menu based on context to trigger specific failures or success
            String targetMenu = "DEPOSIT_SCREEN"; // Default success target relative to MAIN_MENU
            
            // If the aggregate is already at DEPOSIT_SCREEN (violation scenario), this reinforces the violation
            if ("DEPOSIT_SCREEN".equals(getCurrentMenuState(aggregate))) {
                targetMenu = "DEPOSIT_SCREEN";
            }
            
            cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
        assertEquals("MAIN_MENU", event.previousMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect IllegalStateException or IllegalArgumentException based on the invariants
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Helper to inspect aggregate state without exposing getters if not needed elsewhere
    private String getCurrentMenuState(TellerSessionAggregate agg) {
        // Since we can't easily read state back out without getters, and the prompt implies domain logic,
        // we assume the violation setup put us in a state. For the test to be robust, we might rely on
        // the specific exception messages, but for now checking the exception type is sufficient.
        // However, to make the 'Navigation state' violation specific, we need to know what menu we are going to.
        // The logic in execute() checks: if (cmd.menuId().equals(this.currentMenuId))
        // The aggregate setup in the violation step sets currentMenu to "DEPOSIT_SCREEN".
        // So the command must target "DEPOSIT_SCREEN".
        return null; // Not strictly used due to logic in @When block
    }
}