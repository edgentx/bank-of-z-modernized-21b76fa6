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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        aggregate.setOperationalContext("GENERAL");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command execution
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // Scenario: NavigateMenuCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-456");
        // Intentionally NOT calling markAuthenticated
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should be thrown");
        assertTrue(capturedException.getMessage().contains("authenticated") || 
                   capturedException instanceof IllegalStateException);
    }

    // Scenario: NavigateMenuCmd rejected — Sessions must timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-789");
        aggregate.markAuthenticated("teller-02");
        // Set last activity to 2 hours ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        repository.save(aggregate);
    }

    // Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect context
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-101");
        aggregate.markAuthenticated("teller-03");
        // Set restricted context
        aggregate.setOperationalContext("RESTRICTED"); 
        repository.save(aggregate);
    }
}
