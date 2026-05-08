package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.legacybridge.model.RoutingRuleUpdatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Context setup handled in 'When' step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Context setup handled in 'When' step
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Context setup handled in 'When' step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        // This scenario is validated by providing a null or invalid target in the command, 
        // as the aggregate state logic for 'dual-processing' usually refers to the resulting state.
        // We simulate the violation via the command input for this BDD scenario.
        aggregate = new LegacyTransactionRoute("route-invalid-target");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-invalid-version");
        aggregate.setVersioningViolation(true);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            // Determine inputs based on context to cover success and failure cases
            String routeId = aggregate.id();
            String ruleId = "rule-1";
            String newTarget = "MODERN";
            int version = 1;
            Instant date = Instant.now();

            if (routeId.contains("invalid-target")) {
                newTarget = "BOTH"; // Violates single target
            } else if (routeId.contains("invalid-version")) {
                // Version check handled by aggregate internal state flag set in Given step
            }

            var cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, version, date);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof RoutingRuleUpdatedEvent);
        
        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("MODERN", event.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
