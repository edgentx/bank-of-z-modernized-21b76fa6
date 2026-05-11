package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    private List<?> resultEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        String routeId = "route-123";
        aggregate = new LegacyTransactionRoute(routeId);
        // Simulate a versioned state
        aggregate.setCurrentRuleVersion(1);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Data setup placeholder, values are used in the 'When' step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Data setup placeholder
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Data setup placeholder
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            "route-123",
            "rule-A",
            "MODERN",
            Instant.now()
        );
        
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNull(capturedException, "Expected success, but got exception: " + capturedException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        
        Object event = resultEvents.get(0);
        // Checking exact type due to lack of 'instanceof pattern matching' in older java or strict type checks
        assertTrue(event instanceof RoutingRuleUpdatedEvent, "Event should be RoutingRuleUpdatedEvent");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \(modern or legacy\) to prevent dual-processing\.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-violation-dual");
        aggregate.setForceDualProcessingViolation(true);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback\.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-violation-version");
        aggregate.setForceVersioningViolation(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain error exception");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
