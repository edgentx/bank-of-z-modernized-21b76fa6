package com.example.steps;

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

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with a valid, authenticated, active state
        aggregate.hydrate(
            "teller-007",
            true,  // authenticated
            true,  // active
            "MAIN_MENU", // current menu
            Instant.now() // recent activity
        );
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in "a valid TellerSession aggregate"
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with unauthenticated state
        aggregate.hydrate(
            "teller-007",
            false, // NOT authenticated
            true,  // active
            "LOGIN_SCREEN",
            Instant.now()
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with old activity timestamp (simulate timeout)
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(20)); // 20 mins ago (> 15 min timeout)
        aggregate.hydrate(
            "teller-007",
            true,  // authenticated
            true,  // active
            "MAIN_MENU",
            oldTime
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-inactive";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate as authenticated but session inactive (e.g. logged out or locked)
        aggregate.hydrate(
            "teller-007",
            true,  // authenticated
            false, // NOT active
            "LOCKED_SCREEN",
            Instant.now()
        );
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Construct valid command details (default for happy path)
            NavigateMenuCmd cmd = new NavigateMenuCmd(
                aggregate.id(),
                "ACCOUNT_DETAILS",
                "SELECT"
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(aggregate, "Aggregate should not be null");
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_DETAILS", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the message matches one of our invariant violations (optional but good practice)
        String msg = caughtException.getMessage();
        assertTrue(
            msg.contains("authenticated") || msg.contains("timeout") || msg.contains("context"),
            "Exception message should relate to the invariant violated"
        );
    }
}
