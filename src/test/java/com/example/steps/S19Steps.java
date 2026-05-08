package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentMenuId = "MAIN_MENU";
    private String currentAction = "ENTER";

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1"); // Ensure authenticated
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId "session-123" is implied by the aggregate creation
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        currentMenuId = "DEPOSIT_SCREEN";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        currentAction = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Reload from repository to simulate persistence boundary if desired, 
            // though in-memory we can just use the instance.
            var aggOpt = repository.findById(aggregate.id());
            if (aggOpt.isPresent()) {
                NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), currentMenuId, currentAction);
                resultEvents = aggOpt.get().execute(cmd);
                repository.save(aggOpt.get()); // Save changes
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // Scenario: NavigateMenuCmd rejected — A teller must be authenticated to initiate a session.
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT call markAuthenticated. It defaults to false.
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException based on the implementation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Scenario: NavigateMenuCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        aggregate.markExpired(); // Sets lastActivity to past
        repository.save(aggregate);
    }

    // Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect the current operational context.
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_operational_context() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-1");
        aggregate.setContextualErrorMode(); // Manipulates state to fail validation
        repository.save(aggregate);
    }

}
