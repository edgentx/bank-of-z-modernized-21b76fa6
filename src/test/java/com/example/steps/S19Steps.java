package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private static final String TEST_SESSION_ID = "SESSION-123";
    private static final String TEST_TELLER_ID = "TELLER-001";
    private static final String TEST_MENU_ID = "MAIN_MENU";
    private static final String TEST_ACTION = "ENTER";

    private TellerSessionAggregate aggregate;
    private InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAuthenticated(TEST_TELLER_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // Intentionally do not mark authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAuthenticated(TEST_TELLER_ID);
        aggregate.expireSession(); // Force timeout logic
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.markAuthenticated(TEST_TELLER_ID);
        aggregate.setInvalidContext(); // Put in a state where navigation is disallowed
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by constants in steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by constants in steps
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by constants in steps
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(TEST_SESSION_ID, TEST_MENU_ID, TEST_ACTION);
            TellerSessionAggregate agg = repository.findById(TEST_SESSION_ID).orElseThrow();
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Save state changes
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(TEST_MENU_ID, event.menuId());
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException but got: " + caughtException.getClass().getSimpleName());
    }
}
