package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
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
    private String validSessionId = "sess-123";
    private String validMenuId = "MAIN_MENU";
    private String validAction = "ENTER";

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(validSessionId);
        aggregate.markAuthenticated("teller-1"); // Must be authenticated
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by initialization
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by initialization
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by initialization
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            var cmd = new NavigateMenuCmd(validSessionId, validMenuId, validAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        var event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(validSessionId, event.aggregateId());
        assertEquals(validMenuId, event.menuId());
    }

    // Scenario: Rejected - Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(validSessionId);
        // Intentionally NOT calling markAuthenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated") 
            || capturedException.getMessage().contains("timeout")
            || capturedException.getMessage().contains("context"));
    }

    // Scenario: Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(validSessionId);
        aggregate.markAuthenticated("teller-1");
        // Set last activity to 31 minutes ago (Threshold is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    // Scenario: Rejected - Context
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate(validSessionId);
        aggregate.markAuthenticated("teller-1");
        aggregate.setCurrentMenu("CLOSED"); // Simulate a state where navigation is invalid
    }

}
