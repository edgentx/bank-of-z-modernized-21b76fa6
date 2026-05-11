package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21 Feature.
 */
public class S21Steps {

    private ScreenMap aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMap("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // No-op, handled in When context
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // No-op, handled in When context
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd("screen-map-123", "MainMenu", "Desktop", null);
        try {
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
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("invalid-map");
    }

    @When("the RenderScreenCmd command is executed with null screenId")
    public void the_RenderScreenCmd_command_is_executed_with_null_screenId() {
        RenderScreenCmd cmd = new RenderScreenCmd("invalid-map", null, "Desktop", null);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMap("bms-violator");
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void the_RenderScreenCmd_command_is_executed_with_long_screenId() {
        // ScreenId exceeds max length of 10
        String longScreenId = "VeryLongScreenIdName";
        RenderScreenCmd cmd = new RenderScreenCmd("bms-violator", longScreenId, "Desktop", null);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}