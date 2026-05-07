package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Simulate authentication and initialization
        aggregate.authenticate("teller-01"); 
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate initialization, assume current aggregate is the target
        assertNotNull(aggregate);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context setup for command execution
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context setup for command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "OPEN");
            this.resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("OPEN", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String id = "session-unauth";
        aggregate = new TellerSessionAggregate(id);
        // Do NOT authenticate
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.authenticate("teller-01");
        // Force the aggregate to look old by manipulating time or state
        // Here we simulate the aggregate having a last activity time far in the past
        // In a real test, we might use a Clock, but here we just mutate state for coverage
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String id = "session-bad-context";
        aggregate = new TellerSessionAggregate(id);
        aggregate.authenticate("teller-01");
        // Simulate the aggregate being in a state where navigation is blocked (e.g. Lockdown)
        aggregate.lockdown();
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception but command succeeded");
        // Typically we might check for a specific DomainException type, 
        // but checking for IllegalState/Argument satisfies the generic error requirement.
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException);
    }
}
