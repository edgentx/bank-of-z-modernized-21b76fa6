package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(15);
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository(DEFAULT_TIMEOUT);
    
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Default Valid Aggregate Setup
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("sess-123", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated(); // Setup valid authenticated state
        aggregate.setLastActivityAt(Instant.now());
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate ID generation in the Given step
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step construction of the command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step construction of the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // Persist state change
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("sess-unauth", DEFAULT_TIMEOUT);
        // Intentionally do NOT call markAuthenticated()
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("sess-timeout", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated();
        // Set activity to past time beyond timeout
        aggregate.setLastActivityAt(Instant.now().minus(DEFAULT_TIMEOUT.plusSeconds(60)));
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("sess-badnav", DEFAULT_TIMEOUT);
        aggregate.markAuthenticated();
        repo.save(aggregate);
        // The violation logic is inside the aggregate check for valid Menu ID
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // In Java DDD, domain errors are often exceptions (IllegalStateException/IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException 
            || capturedException instanceof IllegalArgumentException);
    }

    // Overload When for the violation scenarios where inputs might differ or we just expect failure
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecutedFailure() {
        // For the context violation, we trigger it by sending a bad menu ID
        String menuId = ""; // Invalid
        if(aggregate.id().equals("sess-badnav")) {
            // Trigger the validation error inside aggregate
             menuId = "";
        } else {
             menuId = "ANY_MENU";
        }
        
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
