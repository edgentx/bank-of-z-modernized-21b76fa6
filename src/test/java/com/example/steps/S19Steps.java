package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId = "session-123";
    private String menuId = "main-menu";
    private String action = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid state defaults to authenticated for happy path
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated();
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsExpired() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.clearEvents();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        sessionId = "session-valid-123";
        // Recreate aggregate to match if needed, or assume existing context
        if (aggregate == null || !sessionId.equals(aggregate.id())) {
             aggregate = new TellerSessionAggregate(sessionId);
             aggregate.markAuthenticated();
             aggregate.clearEvents();
        }
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "menu-withdrawals";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Check if it's an IllegalStateException (Domain Error) or IllegalArgumentException (Validation)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Then("the command is rejected with a domain error for context")
    public void theCommandIsRejectedForContext() {
        theCommandIsRejectedWithADomainError();
        // Ensure specific setup triggers specific error if needed
        // Here we just rely on the exception type check
    }
}