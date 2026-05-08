package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    // Test context variables
    private LegacyTransactionRoute aggregate;
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    
    // Test data
    private String testRouteId = "test-route-24";
    private String validRuleId = "RULE-001";
    private String validTarget = "MODERN";
    private Instant validDate = Instant.now();
    private int validVersion = 2;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Rule ID is hardcoded in test data, step acts as documentation
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Target is hardcoded in test data
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Date is hardcoded in test data
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                testRouteId,
                validRuleId,
                validTarget,
                validDate,
                validVersion
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertEquals("RoutingUpdated", resultEvents.get(0).type(), "Event type should be RoutingUpdated");
    }

    // --- Negative Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Invariants result in IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
