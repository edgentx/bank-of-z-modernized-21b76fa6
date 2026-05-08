package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private static final String SESSION_ID = "sess-123";
    private static final String CURRENT_MENU = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup valid state: Authenticated, Active, Context set
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu(CURRENT_MENU);
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by constructor in 'aValidTellerSessionAggregate'
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Placeholder for command construction logic verification
        // Specific ID used in command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Placeholder for command construction logic verification
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Intentionally do NOT call markAuthenticated()
        aggregate.setCurrentMenu(CURRENT_MENU);
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu(CURRENT_MENU);
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivity(Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());
        // Intentionally do NOT set Current Menu (null)
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "CUSTOMER_SEARCH", "ENTER");
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
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals("CUSTOMER_SEARCH", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check it's not a generic UnknownCommandException, but a domain logic error (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
