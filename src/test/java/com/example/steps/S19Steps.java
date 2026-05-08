package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        currentSessionId = "session-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Setup valid state: authenticated
        aggregate.markAuthenticated("teller-01");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        currentSessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Intentionally do not mark as authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        currentSessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-01");
        // Force the aggregate into a timed out state
        aggregate.forceTimeout();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        currentSessionId = "session-invalid-ctx";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.invalidateContext(); // Mark closed or invalid
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Stored in currentSessionId from previous step
        assertNotNull(currentSessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        currentMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Reload from repository to simulate persistence lifecycle
            TellerSessionAggregate agg = repository.findById(currentSessionId)
                    .orElseThrow(() -> new IllegalStateException("Aggregate not found"));

            NavigateMenuCmd cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
            agg.execute(cmd);
            
            // Save to commit changes
            repository.save(agg);
            this.aggregate = agg;
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Events list should not be empty");
        
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                   "Expected a domain exception (IllegalStateException or IllegalArgumentException)");
    }
}
