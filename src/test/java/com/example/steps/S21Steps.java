package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // State setup handled in 'When' step construction
    }

    @Given("a valid deviceType is provided")
    public void a valid_deviceType_is_provided() {
        // State setup handled in 'When' step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        Command cmd = new RenderScreenCmd("LOGIN_SCR_01", "3270_TERMINAL");
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
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR_01", event.screenId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
    }

    @When("the RenderScreenCmd command is executed with null screenId")
    public void the_RenderScreenCmd_command_is_executed_with_null_screenId() {
        Command cmd = new RenderScreenCmd(null, "3270_TERMINAL");
        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error for mandatory fields")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @When("the RenderScreenCmd command is executed with excessive length")
    public void the_RenderScreenCmd_command_is_executed_with_excessive_length() {
        String longString = "a".repeat(100); // Exceeds 80 char limit
        Command cmd = new RenderScreenCmd(longString, "3270_TERMINAL");
        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error for field length")
    public void the_command_is_rejected_with_a_domain_error_for_length() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("Field length violation"));
    }
}