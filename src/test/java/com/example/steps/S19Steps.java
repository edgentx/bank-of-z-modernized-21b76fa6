package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Initialize the session to a valid authenticated state
        aggregate.execute(new InitiateSessionCmd(id, "teller123", "terminalA"));
        aggregate.clearEvents(); // Clear setup events
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Used in the 'When' step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Used in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Do NOT call InitiateSessionCmd. The session remains unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Initiate
        aggregate.execute(new InitiateSessionCmd(id, "teller123", "terminalA"));
        aggregate.clearEvents();
        
        // Directly mutate lastActivity to simulate timeout (unfortunately necessary for test setup)
        // In a real scenario, time would pass. Here we simulate the state of being timed out.
        aggregate.simulateTimeOut(Duration.ofMinutes(16)); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.execute(new InitiateSessionCmd(id, "teller123", "terminalA"));
        aggregate.clearEvents();
        // Simulate entering a context that doesn't allow navigation (e.g., locked)
        aggregate.simulateEnterState("LOCKED");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        String sessionId = aggregate.id();
        // Using specific values for the command
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "OPEN_ACCT");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("OPEN_ACCT", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error exception");
        // Check for illegal state or argument exceptions as indicators of domain rule enforcement
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
