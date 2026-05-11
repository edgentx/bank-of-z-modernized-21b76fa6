package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    private List<?> resultEvents;

    // Given Steps
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        String routeId = "ROUTE-123";
        aggregate = new LegacyTransactionRoute(routeId);
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // No-op, command construction will use valid string
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // No-op, command construction will use valid string
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // No-op, command construction will use valid Instant
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \(modern or legacy\) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_single_target() {
        a_valid_legacy_transaction_route_aggregate();
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        a_valid_legacy_transaction_route_aggregate();
        aggregate.markVersioningViolation();
    }

    // When Steps
    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "ROUTE-123",
                "RULE-ABC",
                "MODERN",
                Instant.now(),
                2
        );
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Then Steps
    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);

        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("ROUTE-123", event.aggregateId());
        assertEquals("RULE-ABC", event.ruleId());
        assertEquals("MODERN", event.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException depending on invariant type
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
