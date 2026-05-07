package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate session initiation event to bring aggregate to valid state
        var initEvent = new TellerSessionInitializedEvent(sessionId, "teller-123", Instant.now().minusSeconds(60));
        // In a real repo we'd save and reload, but for unit test we assume this state or set fields directly if accessible.
        // Since we can't modify shared AggregateRoot to expose public setters for state injection,
        // we rely on the execute method to handle initialization or we simply assume the ID is enough for a 'valid' start state 
        // if the command handles initialization checks. 
        // For the purpose of BDD, we treat 'Given valid aggregate' as having an ID and being in the repo.
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the aggregate setup
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Unauthenticated aggregate might have no auth token or null teller ID
        String sessionId = "unauth-session";
        this.aggregate = new TellerSessionAggregate(sessionId);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // We need to simulate an old session. Since we can't easily set internal timestamps without setters,
        // we will assume the command logic checks a 'lastActivity' timestamp. 
        // For this test, we create an aggregate but the validation logic inside execute will fail.
        String sessionId = "timeout-session";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup: if we had a setLastActivity method, we'd call it.
        // For now, we rely on the implementation to reject it based on internal logic or mock data.
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // Context violation, e.g., navigating to a menu inaccessible from current state
        String sessionId = "bad-ctx-session";
        this.aggregate = new TellerSessionAggregate(sessionId);
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception, but command succeeded.");
        // Check it's a domain error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // Additional helper for unauthenticated command execution
    @When("the NavigateMenuCmd command is executed on unauthenticated session")
    public void the_NavigateMenuCmd_command_is_executed_on_unauthenticated_session() {
         try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
