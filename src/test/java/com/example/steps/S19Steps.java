package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();

    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated(); // Ensure valid state
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Do not mark authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.expireSession();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.markInvalidContext();
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by constants in the When step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled by constants in the When step
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        // Handled by constants in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Fetch the aggregate to simulate loading from repo
            var loadedAggregate = repository.findById(VALID_SESSION_ID)
                    .orElseThrow(() -> new RuntimeException("Aggregate not found"));
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, VALID_MENU_ID, VALID_ACTION);
            resultEvents = loadedAggregate.execute(cmd);
            
            // Save changes (events are applied inside execute via state mutation, but let's persist)
            repository.save(loadedAggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Domain rules violations are typically IllegalStateExceptions or IllegalArgumentExceptions
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
