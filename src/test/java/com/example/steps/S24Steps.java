package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private String currentRouteId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        currentRouteId = "route-legacy-001";
        LegacyTransactionRoute route = new LegacyTransactionRoute(currentRouteId);
        repository.save(route);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // Scenario context setup, implicit in command construction
    }

    @Given("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // Scenario context setup
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // Scenario context setup
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            Optional<LegacyTransactionRoute> routeOpt = repository.findById(currentRouteId);
            assertTrue(routeOpt.isPresent(), "Route should exist");
            
            LegacyTransactionRoute route = routeOpt.get();
            
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                currentRouteId,
                "RULE-102",
                "MODERN",
                Instant.now().plusSeconds(3600)
            );

            resultEvents = route.execute(cmd);
            route.clearEvents(); // Clear uncommitted events after execution
            repository.save(route);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("MODERN", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        currentRouteId = "route-dual-violation";
        LegacyTransactionRoute route = new LegacyTransactionRoute(currentRouteId);
        // For the Update command, we simulate a state where the command attempts an invalid transition
        // or the aggregate is in a state that prevents a single target update.
        // In this specific command context, we simulate the violation via the command data.
        repository.save(route);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        currentRouteId = "route-version-violation";
        LegacyTransactionRoute route = new LegacyTransactionRoute(currentRouteId);
        repository.save(route);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
