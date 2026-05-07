package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.InitiateTellerSessionCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-condition: Initiate the session so it is active and authenticated
        aggregate.execute(new InitiateTellerSessionCmd(sessionId, "teller-42", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session but do NOT initiate it. It remains unauthenticated.
        aggregate = new TellerSessionAggregate("unauth-session-1");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsTimedOut() {
        String sessionId = "timeout-session-1";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate with a timestamp well in the past
        Instant past = Instant.now().minus(Duration.ofMinutes(30));
        aggregate.execute(new InitiateTellerSessionCmd(sessionId, "teller-timeout", past));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        // While the aggregate itself is valid, the operational context violation
        // is typically tested via invalid inputs (handled in aggregate logic) or specific scenarios.
        // For this step, we ensure a valid aggregate exists, then we will attempt an invalid action in the 'When' step.
        // (Or, if the requirement implies a specific dirty state, we would mock it here.
        //  Assuming the standard aggregate is fine, and validation logic catches context issues).
        aValidTellerSessionAggregate();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicit in the aggregate creation in the Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Data setup happens in the When step construction of the command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Data setup happens in the When step construction of the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Valid command parameters
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNTS", "VIEW_DETAILS");
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @When("the NavigateMenuCmd command is executed on the invalid aggregate")
    public void theNavigateMenuCmdCommandIsExecutedOnInvalidAggregate() {
        try {
            // Attempting to navigate. The aggregate state (determined by 'Given') dictates validity.
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DASHBOARD", "OPEN");
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("ACCOUNTS", event.menuId());
        assertEquals("VIEW_DETAILS", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Verify it's an exception thrown by our domain logic (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
