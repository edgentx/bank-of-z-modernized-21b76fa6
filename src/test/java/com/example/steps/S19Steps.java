package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String sessionId;

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate store;
        @Override public void save(TellerSessionAggregate aggregate) { this.store = aggregate; }
        @Override public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store);
        }
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated(); // Ensure valid state
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by setup
        Assertions.assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context setup handled in 'When' via cmd construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context setup handled in 'When' via cmd construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Constructing a generally valid command for the positive path
            // For negative paths, the aggregate state is manipulated in 'Given'
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @When("the NavigateMenuCmd command is executed on invalid context")
    public void theNavigateMenuCmdCommandIsExecutedInvalidContext() {
        try {
            // Triggering the specific context violation logic defined in the aggregate
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "ANY", "INVALID_CONTEXT");
            this.resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-bad-auth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // 'markAuthenticated' is NOT called, leaving it false
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Helper to force old timestamp
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "sess-bad-context";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Verify it's a domain logic exception (IllegalStateException in our impl)
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}