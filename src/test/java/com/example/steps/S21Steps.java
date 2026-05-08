package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // Assume the aggregate starts in a valid state or has no state required for validation context other than ID
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup handled in execution step
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup handled in execution step
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aggregate_mandatory_fields_violated() {
        aggregate = new ScreenMapAggregate("screen-map-bad-mandatory");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aggregate_bms_constraints_violated() {
        aggregate = new ScreenMapAggregate("screen-map-bad-bms");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Scenario 1: Valid Data
        if ("screen-map-1".equals(aggregate.id())) {
            RenderScreenCmd cmd = new RenderScreenCmd("scr-001", "3270", Map.of("field1", "value"));
            resultEvents = aggregate.execute(cmd);
        }
        // Scenario 2: Missing Mandatory (e.g. null screenId)
        else if ("screen-map-bad-mandatory".equals(aggregate.id())) {
            try {
                RenderScreenCmd cmd = new RenderScreenCmd(null, "3270", Map.of());
                aggregate.execute(cmd);
            } catch (IllegalArgumentException e) {
                capturedException = e;
            }
        }
        // Scenario 3: BMS Constraint Violation (e.g. screenId too long)
        else if ("screen-map-bad-bms".equals(aggregate.id())) {
            try {
                RenderScreenCmd cmd = new RenderScreenCmd("this-id-is-much-too-long-for-legacy-bms", "3270", Map.of());
                aggregate.execute(cmd);
            } catch (IllegalArgumentException e) {
                capturedException = e;
            }
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }
}