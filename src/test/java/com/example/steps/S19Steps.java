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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String targetMenuId;
    private String action;

    // --- Scenario 1: Success ---
    
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Hydrate to valid state (Authenticated, Active)
        aggregate.hydrate("teller-01", true, Instant.now(), "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate creation
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.targetMenuId = "ACCOUNT_DETAILS";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent);
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(targetMenuId, navEvent.targetMenuId());
        assertEquals(action, navEvent.action());
    }

    // --- Scenario 2: Auth Failure ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-999");
        // Hydrate with isAuthenticated = false
        aggregate.hydrate(null, false, Instant.now(), "LOGIN_SCREEN");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // --- Scenario 3: Timeout Failure ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Hydrate with a timestamp 20 minutes ago
        Instant past = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.hydrate("teller-01", true, past, "MAIN_MENU");
    }

    // Reuse When/Then from above

    // --- Scenario 4: Context Failure ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        // Hydrate as authenticated but with a sessionActive flag false internally (simulated via lack of hydration or explicit flag if needed)
        // Since hydrate sets active=true by default in our impl, we use reflection or (cleaner) a constructor/setup method that sets it false.
        // For this DDD style, let's assume the aggregate was created but not properly activated.
        // We'll mock the internal state simply by NOT hydrating it fully, but we need isAuthenticated to be true to pass the first guard.
        // Let's rely on the fact that 'hydrate' sets active=true. To violate context, let's create a NEW aggregate and set ONLY auth, leaving active false if default is false.
        // The default constructor in our impl sets sessionActive = false.
        // So we just need to set auth true without calling hydrate.
        aggregate = new TellerSessionAggregate("session-ctx") {
            // Anonymous subclass to set protected fields if needed, or add a setter to the aggregate for testing.
            // Our aggregate doesn't have a setter for isAuthenticated publicly. Let's rely on hydrate to set active=false.
        };
        // Actually, let's just add a test helper to the Aggregate or assume a specific state.
        // For simplicity in steps, let's assume we have a method to set active = false.
        // However, TellerSessionAggregate has no such public setter.
        // Workaround: The invariants are checked inside navigateMenu.
        // If sessionActive is false (default), it fails.
        // We just need isAuthenticated true. Let's extend the class in the test to access protected state or assume the class allows it.
        // Since I cannot change TellerSessionAggregate to add a `setActive` method just for testing (domain integrity),
        // I will assume the TellerSessionAggregate was initialized in a way that `sessionActive` is false.
        
        // Re-creating an aggregate in a 'corrupted' state.
        TellerSessionAggregate broken = new TellerSessionAggregate("session-ctx") {
             // I can't access private fields here easily.
        };
        
        // Best approach: create a valid aggregate, but use a reflection helper or add a test-only method.
        // Given the constraints, I will instantiate the aggregate and use the 'hydrate' method, but if hydrate forces active=true, I cannot fail this specific invariant easily without reflection.
        // Alternative: The invariant "Navigation state must accurately reflect..." might imply that the MenuID doesn't exist in the registry. 
        // But the description says "violates: Navigation state...".
        // Let's interpret this as the session being inactive (e.g. locked).
        // I will leave aggregate NOT hydrated. isAuthenticated defaults to false. 
        // WAIT: If I do that, I hit Scenario 2 (Auth).
        // I need Auth=True AND Active=False.
        
        // Since I cannot modify TellerSessionAggregate to support this state easily without a setter, I will verify the failure based on a null reference or similar check inside the command handler logic if applicable.
        // BUT, looking at TellerSessionAggregate: sessionActive defaults to FALSE. 
        // isAuthenticated defaults to FALSE. 
        // hydrate() sets BOTH to true.
        // I cannot construct a state where Auth=True AND Active=False with the current API.
        
        // PIVOT: I will use reflection to force the state for the test.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("isAuthenticated");
            field.setAccessible(true);
            field.set(aggregate, true);
            // sessionActive remains false (default)
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }
    }
}