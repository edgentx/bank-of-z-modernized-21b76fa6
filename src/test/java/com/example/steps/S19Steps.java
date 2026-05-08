package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Simulate initialization via event to establish valid state without a full Init command
        // In a real scenario we would issue an Init cmd, here we bootstrap for the specific test
        // However, to satisfy "must be authenticated", we manually set the state or issue a bootstrap command.
        // Let's assume the aggregate starts in a state that requires authentication.
        // For the "Success" scenario, we need to make it authenticated and active.
        aggregate.markAuthenticated(); // Helper to bypass full auth flow for unit testing navigation logic
        aggregate.markActive(Instant.now().plus(Duration.ofHours(1)));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // ID is handled in the aggregate creation step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // MenuId is part of the command, handled in 'When'
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Action is part of the command, handled in 'When'
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        String sessionId = aggregate.id();
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Intentionally NOT marking authenticated. Default is unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        // Set last activity time to 2 hours ago (assuming timeout is 15 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.markActive(Instant.now().plus(Duration.ofHours(1)));
        // Force the aggregate into a state where it cannot accept navigation (e.g., locked)
        aggregate.lock();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check for specific domain exceptions or illegal state
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException);
    }

    @When("the NavigateMenuCmd command is executed")
    public void executeNavigateMenuCmd() {
        // Re-using the method name for Cucumber mapping, though duplicate in Gherkin map
        // Logic is identical to the first When step.
        String sessionId = aggregate.id();
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MENU_01", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
