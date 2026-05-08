package com.example.steps;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.screen.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context handled in the When step via command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context handled in the When step via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Using valid data for the happy path
            Map<String, String> fields = new HashMap<>();
            fields.put("field1", "value1");
            RenderScreenCmd cmd = new RenderScreenCmd("screen-101", "3270", fields);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) events.get(0);
        Assertions.assertEquals("screen-101", event.screenId());
    }

    // Scenario 2: Validation Error (Mandatory Fields)
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("screen-map-2");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedForValidation() {
        try {
            // Missing screenId (null or blank) triggers validation error
            RenderScreenCmd cmd = new RenderScreenCmd("", "3270", new HashMap<>());
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
        Assertions.assertTrue(capturedException.getMessage().contains("required"));
    }

    // Scenario 3: Validation Error (BMS Constraints)
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedForBMS() {
        try {
            Map<String, String> fields = new HashMap<>();
            // Legacy BMS map name is often limited to 8 chars, or screenId to 8.
            // Let's assume screenId must be <= 8 chars.
            fields.put("f1", "v1");
            RenderScreenCmd cmd = new RenderScreenCmd("very-long-screen-id", "3270", fields);
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // Inner mock class for repository if needed by other patterns, though aggregate logic is self-contained
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();

        @Override
        public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }

        @Override
        public ScreenMapAggregate findById(String id) {
            return store.get(id);
        }
    }
}
