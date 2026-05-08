package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRouteAggregate aggregate;
    private EvaluateRoutingCmd cmd;
    private List<DomainEvent> result;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = repository.createOrGet("route-123");
        assertNotNull(aggregate);
    }

    @And("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // Handled in construction of command in 'When' step, or setup here
    }

    @And("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // Handled in 'When' step
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        // Default valid command
        if (cmd == null) {
            cmd = new EvaluateRoutingCmd("route-123", "DEPOSIT", Map.of("amount", 100), null, null);
        }
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof RoutingEvaluatedEvent);
        
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) result.get(0);
        assertEquals("route-123", event.aggregateId());
        assertEquals("routing.evaluated", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = repository.createOrGet("route-456");
        // We set up the command to explicitly request a violating state
        cmd = new EvaluateRoutingCmd("route-456", "DEPOSIT", Map.of(), null, "DUAL");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = repository.createOrGet("route-789");
        // The aggregate assumes Version 1. Requesting Version 2 violates the safe rollback/versioning rule.
        cmd = new EvaluateRoutingCmd("route-789", "DEPOSIT", Map.of(), 2, null);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify message content for correctness
        String message = thrownException.getMessage();
        if (cmd.explicitTargetSystem() != null && cmd.explicitTargetSystem().equals("DUAL")) {
            assertTrue(message.contains("exactly one backend system"));
        } else if (cmd.targetRulesVersion() != null && cmd.targetRulesVersion() == 2) {
            assertTrue(message.contains("Routing rules version mismatch"));
        }
    }
}
