package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.*;
import com.example.domain.uinavigation.repository.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        // Create a valid aggregate instance
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        // Optionally load initial state or verify existence in repository
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Assumption: Logic handled within the 'When' step construction of the command
    }

    @Given("a valid deviceType is provided")
    public void a valid_deviceType_is_provided() {
        // Assumption: Logic handled within the 'When' step construction of the command
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("invalid-map-1");
        // We simulate the violation by passing nulls in the command later
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMapAggregate("invalid-map-2");
        // We simulate the violation by passing oversized strings in the command later
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // We assume context setup in previous steps determines which branch we test.
        // However, since Cucumber steps are stateless regarding 'which scenario', we rely on
        // specific step definitions or a shared context variable. 
        // For simplicity in this generated code, we check the current aggregate ID or similar, 
        // or just execute a happy path if not specified. 
        // Ideally, we'd have specific When steps for each scenario context.
        
        try {
            Command cmd;
            if (aggregate.id().equals("screen-map-1")) {
                // Happy path
                cmd = new RenderScreenCmd("screen-101", "DESKTOP", Map.of());
            } else if (aggregate.id().equals("invalid-map-1")) {
                // Missing fields
                cmd = new RenderScreenCmd(null, "DESKTOP", Map.of());
            } else {
                // Invalid length
                cmd = new RenderScreenCmd("screen-101", "A_VERY_LONG_DEVICE_TYPE_THAT_EXCEEDS_BMS_LIMIT", Map.of());
            }

            List<DomainEvent> events = aggregate.execute(cmd);
            // Apply events to mutate aggregate if needed (handled in execute)
            // Commit to repo if we were testing persistence
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have emitted an event");
        Assertions.assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain error (exception)");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
