package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the When step construction for simplicity in this context
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the When step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid data for the success case
        cmd = new RenderScreenCmd("screen-map-1", "SCRN001", "3270");
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data(String screenId, String deviceType) {
        // Parameterized injection is tricky in raw cucumber without data tables, 
        // so we rely on specific scenario contexts set up in Given steps if needed.
        // For simplicity, we capture the specific violations here if triggered by specific Givens.
        if (screenId == null) screenId = "";
        if (deviceType == null) deviceType = "";
        
        cmd = new RenderScreenCmd("screen-map-1", screenId, deviceType);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // --- Scenario 2 Specifics ---
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
    }
    
    // Reuse generic When/Then, but we need to trigger the specific validation.
    // In a real runner, we'd use a Scenario Outline or specific parameters.
    // Here we manually trigger the execution with bad data for the specific Given context.
    @When("the RenderScreenCmd command is executed with blank mandatory fields")
    public void execute_cmd_blank_mandatory() {
        cmd = new RenderScreenCmd("screen-map-2", "", "3270"); // Blank screenId
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Scenario 3 Specifics ---
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @When("the RenderScreenCmd command is executed with invalid BMS length")
    public void execute_cmd_invalid_bms_length() {
        cmd = new RenderScreenCmd("screen-map-3", "SCREEN-ID-IS-WAY-TOO-LONG", "3270");
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
