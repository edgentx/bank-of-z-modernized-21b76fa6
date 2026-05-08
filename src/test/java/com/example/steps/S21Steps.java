package com.example.steps;

import com.example.domain.navigation.model.DeviceType;
import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMap screenMap;
    private RenderScreenCmd cmd;
    private Exception caughtException;
    private java.util.List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        screenMap = new ScreenMap("map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Setup handled in When step via explicit construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Setup handled in When step via explicit construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_invalid_mandatory_fields() {
        screenMap = new ScreenMap("map-1");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_bms_length() {
        screenMap = new ScreenMap("map-1");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Default valid command, overridden in specific scenarios if needed, 
            // but here we construct based on the context implied by previous steps or defaults.
            // For negative tests, we will set the cmd explicitly.
            if (cmd == null) {
                cmd = new RenderScreenCmd("map-1", "SCRN01", DeviceType.WEB_DESKTOP);
            }
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("map-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    // Specific step implementations for the negative scenarios to drive the cmd state
    @And("the screenId is null")
    public void the_screen_id_is_null() {
        cmd = new RenderScreenCmd("map-1", null, DeviceType.WEB_DESKTOP);
    }

    @And("the screenId length exceeds BMS limits")
    public void the_screen_id_length_exceeds_bms_limits() {
        cmd = new RenderScreenCmd("map-1", "VERY_LONG_SCREEN_ID", DeviceType.WEB_DESKTOP);
    }

    @And("the deviceType is null")
    public void the_device_type_is_null() {
        cmd = new RenderScreenCmd("map-1", "SCRN01", null);
    }
}
