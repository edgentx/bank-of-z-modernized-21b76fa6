package com.example.steps;

import com.example.domain.navigation.model.DeviceType;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-21: RenderScreenCmd.
 */
public class S21Steps {

    private ScreenMap aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMap("screen-001");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in construction of command in 'When' or separate steps if needed.
        // For this scenario, we assume valid data unless specified otherwise.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in construction of command
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Construct a valid command
            cmd = new RenderScreenCmd(
                "login-screen",
                DeviceType.DESKTOP,
                "layout-default",
                Map.of("username", "user1", "password", "pass")
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("screen.rendered", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("screen-002");
        // The violation will be triggered by the command payload in 'When'
    }

    @When("the RenderScreenCmd command is executed with empty input")
    public void the_command_is_executed_with_empty_input() {
        try {
            // Simulate missing/empty mandatory input fields
            cmd = new RenderScreenCmd(
                "details-screen",
                DeviceType.MOBILE,
                "layout-mobile",
                Map.of() // Empty map violates validation
            );
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMap("screen-003");
    }

    @When("the RenderScreenCmd command is executed with long fields")
    public void the_command_is_executed_with_long_fields() {
        try {
            // Simulate a field exceeding BMS length (max 80)
            String longString = "x".repeat(81);
            cmd = new RenderScreenCmd(
                "bms-screen",
                DeviceType.TERMINAL_3270,
                "layout-3270",
                Map.of("longField", longString)
            );
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents); // No events should be emitted on failure
    }
}
