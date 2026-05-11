package com.example.steps;

import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;

    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();
        @Override
        public ScreenMapAggregate load(String id) {
            return store.getOrDefault(id, new ScreenMapAggregate(id));
        }
        @Override
        public void save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context is implicitly handled by the aggregate initialization or command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context is implicitly handled by the aggregate initialization or command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Assuming ScreenMapAggregate constructor sets the screenId internally or we use a specific command
            // For this scenario, we assume the aggregate ID corresponds to the screenId being rendered
            // and we are rendering for a standard "WEB" device type.
            // If specific screenId/deviceType injection into the aggregate state was required before command,
            // we would need a setter or a factory, but standard DDD aggregates are usually loaded by ID.
            // We will use the command to specify the rendering context.
            
            // However, the ScreenMapAggregate instance holds the state. The command triggers the change.
            // Since ScreenRenderedEvent carries device info, the command must too.
            // We construct the command with parameters suitable for a valid scenario.
            
            // To satisfy the repository pattern usage seen in other tests (load -> execute -> save):
            var agg = repository.load(aggregate.id());
            
            // Constructing a valid command. 
            // If ScreenMapAggregate needs specific state to be 'valid', we'd need to hydrate it.
            // Assuming new ScreenMapAggregate("screen-1") is valid enough to render.
            
            Command cmd = new RenderScreenCmd("screen-1", "3270", Map.of());
            List<DomainEvent> events = agg.execute(cmd);
            repository.save(agg);
            
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no error, but got: " + capturedException);
        var agg = repository.load("screen-1");
        List<DomainEvent> events = agg.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Expected ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("screen-invalid-mandatory");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("screen-invalid-bms");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain error to be thrown");
        // Verify it's a specific domain error type (IllegalArgument or IllegalState)
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException"
        );
    }

    // Overload When for negative scenarios to inject specific invalid command data
    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        try {
            var agg = repository.load(aggregate.id());
            Command cmd;
            
            // Heuristic to determine which failure mode based on the aggregate ID string for demo purposes
            if (aggregate.id().contains("mandatory")) {
                // Pass null or empty for mandatory fields (e.g. screenId)
                cmd = new RenderScreenCmd(null, "WEB", Map.of());
            } else {
                // Pass a deviceType exceeding legacy constraints
                cmd = new RenderScreenCmd("screen-1", "DEVICE_TYPE_TOO_LONG_FOR_BMS", Map.of());
            }
            
            agg.execute(cmd);
            repository.save(agg);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            this.capturedException = e;
        }
    }
}
