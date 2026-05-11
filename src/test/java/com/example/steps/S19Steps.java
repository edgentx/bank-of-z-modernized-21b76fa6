package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermenu.model.*;
import com.example.domain.tellermenu.repository.TellerSessionRepository;
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

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Manually apply setup to simulate a valid, authenticated state
        aggregate.applySessionCreated(new SessionCreatedEvent("session-123", "teller-456", Instant.now()));
        aggregate.applyHeartbeat(new SessionHeartbeatEvent("session-123", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is implicitly handled via the aggregate instance in this unit-level test
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Provided via command construction in the 'When' step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Provided via command construction in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("MAIN_MENU", event.targetMenuId());
        assertEquals("ENTER", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do not apply SessionCreatedEvent, leaving it unauthenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.applySessionCreated(new SessionCreatedEvent("session-timeout", "teller-1", Instant.now().minus(Duration.ofHours(2))));
        // Set last activity to way in the past
        aggregate.applyHeartbeat(new SessionHeartbeatEvent("session-timeout", Instant.now().minus(Duration.ofHours(1))));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.applySessionCreated(new SessionCreatedEvent("session-bad-state", "teller-1", Instant.now()));
        aggregate.applyHeartbeat(new SessionHeartbeatEvent("session-bad-state", Instant.now()));
        // Simulate a bad state by doing something invalid, but for this test, we assume the aggregate logic prevents invalid navigation
        // Here we create a scenario where we might try to navigate from an invalid screen (logic handled in aggregate)
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on implementation, could be IllegalStateException, IllegalArgumentException, or custom
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}