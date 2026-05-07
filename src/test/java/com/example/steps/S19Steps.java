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
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate session;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.session = new TellerSessionAggregate(sessionId);
        this.session.markAuthenticated("teller-001");
        this.session.setLastActivityAt(Instant.now());
        this.session.setCurrentContext("MAIN_MENU");
        this.repo.save(this.session);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in context
        assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "ACCOUNT_DETAILS";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Retrieve fresh session to simulate repository fetch
            TellerSessionAggregate agg = repo.findById(this.sessionId).orElseThrow();
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action, "teller-001", "MAIN_MENU");
            this.resultEvents = agg.execute(cmd);
            // Update session state in repo (command handler side effect)
            repo.save(agg);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(this.resultEvents);
        assertEquals(1, this.resultEvents.size());
        assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(this.menuId, event.menuId());
        assertEquals(this.action, event.action());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "session-unauth";
        this.session = new TellerSessionAggregate(sessionId);
        // NOT marking authenticated
        this.repo.save(this.session);
        this.menuId = "ADMIN";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.session = new TellerSessionAggregate(sessionId);
        this.session.markAuthenticated("teller-001");
        // Set activity to 20 minutes ago
        this.session.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        this.repo.save(this.session);
        this.menuId = "MAIN";
        this.action = "REFRESH";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "session-bad-ctx";
        this.session = new TellerSessionAggregate(sessionId);
        this.session.markAuthenticated("teller-001");
        this.session.setLastActivityAt(Instant.now());
        this.session.setCurrentContext("TRANSACTION_SCREEN");
        this.repo.save(this.session);
        this.menuId = "ADMIN";
        this.action = "ENTER";
        // The command will attempt to use MAIN_MENU context, but aggregate has TRANSACTION_SCREEN
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
