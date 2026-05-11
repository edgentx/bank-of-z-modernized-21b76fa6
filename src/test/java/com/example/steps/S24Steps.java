package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute route;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String providedRuleId = "RULE-101";
    private String providedNewTarget = "VForce360";
    private Instant providedEffectiveDate = Instant.now().plusSeconds(3600);

    // Scenario 1 & 3: Valid Aggregate
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        route = new LegacyTransactionRoute("route-1");
        assertNotNull(route);
    }

    // Scenario 2: Dual Processing Violation
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_with_dual_processing_violation() {
        route = new LegacyTransactionRoute("route-dual");
        route.markDualProcessingViolation();
    }

    // Scenario 3: Versioning Violation
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_with_versioning_violation() {
        route = new LegacyTransactionRoute("route-version");
        route.markVersioningViolation();
    }

    @And("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        providedRuleId = "RULE-101";
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        providedNewTarget = "VForce360";
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        providedEffectiveDate = Instant.now().plusSeconds(3600);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            var cmd = new UpdateRoutingRuleCmd(
                route.id(),
                providedRuleId,
                providedNewTarget,
                providedEffectiveDate,
                5 // valid version
            );
            resultEvents = route.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0).type().contains("RoutingUpdated"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
