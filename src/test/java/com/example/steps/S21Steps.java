package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Command construction happens in the When step
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Command construction happens in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        command = new RenderScreenCmd("LOGIN_SCR", "3270");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("3270", event.deviceType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        // We pass nulls via the command in the 'When' step
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_data() {
        command = new RenderScreenCmd(null, null); // Violating mandatory fields
        try {
            resultEvents = aggregate.execute(command);
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
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @When("the RenderScreenCmd command is executed with long fields")
    public void the_RenderScreenCmd_command_is_executed_with_long_fields() {
        command = new RenderScreenCmd("THIS_SCREEN_ID_IS_WAY_TOO_LONG_FOR_LEGACY", "3270");
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
