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

import static org.junit.Assert.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Positive Scenario Setup
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a valid, authenticated, non-expired session
        String sessionId = "sess-123";
        String tellerId = "teller-01";
        Instant now = Instant.now();
        
        aggregate = new TellerSessionAggregate(sessionId);
        // Force state to valid via reflection or default constructor logic if available. 
        // Since we have a strict aggregate, we simulate a 'SessionStarted' event to hydrate it.
        // However, to keep steps simple and isolated to S-19, we assume the aggregate starts 
        // in a valid state if we instantiate it and set fields (simulating hydration).
        // We will use the constructor and assume defaults handle the basics or test hydration directly.
        
        // Simulating hydrated state:
        aggregate.hydrate(tellerId, "MAIN_MENU", now, Duration.ofMinutes(15));
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate instantiation
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in the command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in the command
    }

    // Negative Scenarios Setup
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // No teller authenticated (tellerId is null/empty)
        aggregate.hydrate(null, "LOGIN", Instant.now(), Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Last activity was 20 mins ago, timeout is 15 mins
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.hydrate("teller-01", "MAIN_MENU", past, Duration.ofMinutes(15));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "sess-badstate";
        aggregate = new TellerSessionAggregate(sessionId);
        // Current state is LOCKED, cannot navigate
        aggregate.hydrate("teller-01", "LOCKED", Instant.now(), Duration.ofMinutes(15));
    }

    // Execution
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            String targetMenu = "ACCOUNT_DETAILS";
            String action = "ENTER";
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            resultEvents = aggregate.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    // Outcomes
    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("ACCOUNT_DETAILS", event.targetMenu());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // In strict DDD, invariants throw IllegalStateExceptions or IllegalArgumentExceptions
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
