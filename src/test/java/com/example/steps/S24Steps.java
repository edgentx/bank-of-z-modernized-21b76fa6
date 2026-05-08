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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class S24Steps {

    // Use the existing InMemory Repository located in the steps package
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-test-1");
        repository.save(aggregate);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Data setup is handled in the 'When' step construction
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Data setup is handled in the 'When' step construction
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Data setup is handled in the 'When' step construction
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                    "route-test-1",
                    "RULE-101",
                    "MODERN",
                    Instant.now().plusSeconds(60)
            );
            // Reload aggregate to simulate clean state if necessary, or use instance directly
            aggregate = repository.findById("route-test-1").orElseThrow();
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist changes
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        assertNotNull(resultEvents);
        assertTrue(resultEvents.size() > 0, "Expected at least one event");
        assertTrue(resultEvents.get(0).type().contains("RoutingUpdated"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-violation-dual");
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-violation-version");
        aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
