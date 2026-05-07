package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "SESSION-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a valid, authenticated, active session state
        aggregate.authenticate("TELLER-1");
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is handled in aggregate creation
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // menuId will be provided in the When step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // action will be provided in the When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "SESSION-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally do NOT authenticate
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-1");
        // Simulate timeout
        aggregate.expireSession(); 
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        String sessionId = "SESSION-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-1");
        // Simulate a state where a menu action is invalid (e.g. attempting a transaction action without context)
        // For this BDD, we mark the aggregate in a way that navigation fails
        aggregate.invalidateNavigationContext();
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "SELECT_OPTION");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("teller.session.menu.navigated", resultEvents.get(0).type());
        assertNull(caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
