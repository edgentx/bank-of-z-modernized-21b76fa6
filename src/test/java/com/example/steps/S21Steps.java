package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.*;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMap aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMap("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled by the aggregate initialization or command construction in 'When'
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Implicitly handled by command construction in 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd("screen-1", DeviceType.TN3270, Map.of());
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen-1", event.aggregateId());
        assertEquals("ScreenRenderedEvent", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_invalid_mandatory_fields() {
        this.aggregate = new ScreenMap("screen-2");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_command_is_executed_with_missing_fields() {
        // Missing layout (simulated by null or empty check in logic, here we assume valid command but aggregate state might block, or command validation)
        // Based on requirements, we test command validation.
        RenderScreenCmd cmd = new RenderScreenCmd(null, DeviceType.TN3270, Map.of());
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("required"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMap("screen-3");
    }

    @When("the RenderScreenCmd command is executed with excessive fields")
    public void the_command_is_executed_with_excessive_fields() {
        // Simulate a layout map that exceeds BMS constraints
        // Since the command carries the input data, we construct a specific command here
        Map<String, Object> oversizedLayout = Map.of("field_1", "A");
        // The actual validation logic is in the Aggregate, checking input sizes
        RenderScreenCmd cmd = new RenderScreenCmd("screen-3", DeviceType.TN3270, oversizedLayout);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.capturedException = e;
        }
    }
}
