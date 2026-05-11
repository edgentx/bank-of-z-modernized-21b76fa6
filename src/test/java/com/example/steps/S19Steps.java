package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Ensure valid state for positive case
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.markUnauthenticated();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.markExpired();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_operational_context() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.markOperationalContextInvalid();
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is initialized in the "Given a valid..." steps
        Assertions.assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    // --- When Steps ---

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Reload from repo to ensure we are testing aggregate state logic
            TellerSessionAggregate agg = repository.findById(this.sessionId)
                    .orElseThrow(() -> new IllegalStateException("Aggregate not found"));
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            agg.execute(cmd);
            
            repository.save(agg);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Then Steps ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        TellerSessionAggregate result = repository.findById(this.sessionId).orElseThrow();
        Assertions.assertFalse(result.uncommittedEvents().isEmpty());
        Assertions.assertEquals("menu.navigated", result.uncommittedEvents().get(0).type());
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Asserting it's an IllegalStateException or similar domain error type
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException);
        System.out.println("Caught expected error: " + caughtException.getMessage());
    }

    // --- In-Memory Repository Implementation ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final Map<String, TellerSessionAggregate> store = new HashMap<>();

        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }

        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
    }
}
