package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermenu.model.*;
import com.example.domain.tellermenu.repository.TellerSessionRepository;
import com.example.domain.tellermenu.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "ts-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initialize(new TellerSessionInitializedEvent(sessionId, "teller-1", Instant.now().minusSeconds(60)));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate setup
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("ts-123", "MENU_WITHDRAWAL", "ENTER");
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
        assertEquals("MENU_WITHDRAWAL", event.targetMenuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        String sessionId = "ts-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do not initialize, or initialize with unauthenticated state
        // For this aggregate, initialized=false implies unauthenticated context
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "ts-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize with a timestamp well beyond the timeout window (e.g., 30 minutes ago)
        aggregate.initialize(new TellerSessionInitializedEvent(sessionId, "teller-1", Instant.now().minusSeconds(3600)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String sessionId = "ts-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initialize(new TellerSessionInitializedEvent(sessionId, "teller-1", Instant.now().minusSeconds(60)));
        // Force the state to be invalid or 'closed' context
        aggregate.closeSession(); 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException);
    }
}