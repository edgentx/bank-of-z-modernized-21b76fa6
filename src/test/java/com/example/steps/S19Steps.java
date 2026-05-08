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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";

    // Scenario: Successfully execute NavigateMenuCmd

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true); // Setup invariant compliance
        // Ensure time is within limits
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by constant in setup, effectively idempotent here
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by constant
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by constant
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, VALID_MENU_ID, VALID_ACTION);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(VALID_SESSION_ID, navEvent.aggregateId());
        assertEquals(VALID_MENU_ID, navEvent.menuId());
        assertEquals(VALID_ACTION, navEvent.action());
    }

    // Scenario: Authentication Failure

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(false); // Violate invariant
        aggregate.setLastActivityAt(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Scenario: Timeout Failure

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        
        // Setup timeout: Set activity to 20 minutes ago (assuming 15 min threshold)
        aggregate.setTimeoutThreshold(Duration.ofMinutes(15));
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    // Scenario: Operational Context Failure

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        
        // Set context to LOCKED to trigger rejection logic
        aggregate.setOperationalContext("LOCKED");
    }

}
