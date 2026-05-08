package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
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
    private RenderScreenCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Placeholder context; command is constructed in 'When'
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Placeholder context; command is constructed in 'When'
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_violation_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-violation-1");
        // The violation will be in the command (null/empty inputFields)
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_violation_bms_constraints() {
        this.aggregate = new ScreenMapAggregate("screen-map-violation-2");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            if (aggregate.getId().equals("screen-map-123")) {
                // Success case inputs
                Map<String, String> inputs = new HashMap<>();
                inputs.put("accountNum", "1234567890");
                this.command = new RenderScreenCmd(aggregate.getId(), "LOGIN_SCREEN", "TN3270", inputs);
            } else if (aggregate.getId().equals("screen-map-violation-1")) {
                // Violation: Mandatory fields (inputFields is empty)
                this.command = new RenderScreenCmd(aggregate.getId(), "LOGIN_SCREEN", "TN3270", new HashMap<>());
            } else if (aggregate.getId().equals("screen-map-violation-2")) {
                // Violation: BMS constraints (input value too long)
                Map<String, String> inputs = new HashMap<>();
                // Length 80 exceeds MAX_FIELD_LENGTH (79)
                inputs.put("longField", "x".repeat(80));
                this.command = new RenderScreenCmd(aggregate.getId(), "LOGIN_SCREEN", "TN3270", inputs);
            }
            this.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals(aggregate.getId(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
        
        if (aggregate.getId().equals("screen-map-violation-1")) {
            Assertions.assertTrue(capturedException.getMessage().contains("mandatory input fields"));
        } else if (aggregate.getId().equals("screen-map-violation-2")) {
            Assertions.assertTrue(capturedException.getMessage().contains("BMS constraints"));
        }
    }
}