package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // We construct the command in the 'When' clause using valid defaults, 
        // so this step essentially just validates our test data setup intent.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Same as above.
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-invalid");
        // The violation comes from the command data in the 'When' step.
        // We can set a flag or context here if needed, but we'll handle it in When.
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMapAggregate("screen-map-long");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Scenario determination based on context or current state is implicit in test setup.
        // We need a default command, but the Gherkin steps imply context-specific data.
        // We will check the aggregate ID to determine which scenario path to take for this demo.
        
        String screenId = "LOGIN_SCREEN";
        String deviceType = "3270";
        Map<String, String> inputs = Map.of("username", "testuser");

        if ("screen-map-invalid".equals(aggregate.id())) {
            // Violation: Empty mandatory field
            inputs = Map.of("username", "");
        } else if ("screen-map-long".equals(aggregate.id())) {
            // Violation: Length > 80
            inputs = Map.of("data", "*".repeat(81));
        }

        this.cmd = new RenderScreenCmd(screenId, deviceType, inputs);

        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("screen-map-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect an IllegalArgumentException which we treat as a domain error in the aggregate
        assertTrue(capturedException instanceof IllegalArgumentException);
        
        String message = capturedException.getMessage();
        if ("screen-map-invalid".equals(aggregate.id())) {
            assertTrue(message.contains("missing") || message.contains("empty"));
        } else if ("screen-map-long".equals(aggregate.id())) {
            assertTrue(message.contains("length exceeds"));
        }
    }
}
