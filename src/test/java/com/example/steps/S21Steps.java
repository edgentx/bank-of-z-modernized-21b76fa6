package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // We build the command in parts or store state in variables to combine later
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Setup valid command data
        cmd = new RenderScreenCmd("map-1", "LOGIN_SCREEN", "DESKTOP");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("map-1", event.aggregateId());
        assertEquals("LOGIN_SCREEN", event.screenId());
        assertEquals("DESKTOP", event.deviceType());
    }

    // Scenario 2: Validation Error
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-2");
        // Create command with null/blank screenId
        cmd = new RenderScreenCmd("map-2", "", "MOBILE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    // Scenario 3: Legacy Constraints Error
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_legacy_constraints() {
        aggregate = new ScreenMapAggregate("map-3");
        // Create command with screenId > 80 chars
        String longScreenId = "A".repeat(81);
        cmd = new RenderScreenCmd("map-3", longScreenId, "TERMINAL");
    }
}
