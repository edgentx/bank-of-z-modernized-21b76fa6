package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private String sessionId;
    private String menuId;
    private String action;
    
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Default to authenticated for valid scenarios
        aggregate.markAuthenticated("TELLER-01"); 
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        sessionId = "TS-UNAUTH-001";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated to violate invariant
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "TS-TIMEOUT-001";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Force timeout state
        aggregate.markTimedOut();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        sessionId = "TS-CTX-001";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Set a context that would theoretically reject a standard navigation request
        // based on the specific logic implemented in the aggregate
        aggregate.setInvalidContext("LOCKED_SCREEN");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already initialized in Given blocks
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        var cmd = new NavigateMenuCmd(sessionId, menuId, action);
        // Fetch aggregate to simulate reloading from store
        var agg = repository.findById(sessionId).orElseThrow();
        
        try {
            resultEvents = agg.execute(cmd);
            // Save state changes if successful
            repository.save(agg);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Domain logic errors are typically IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        System.out.println("Caught expected exception: " + capturedException.getMessage());
    }
}
