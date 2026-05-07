package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context setup handled in the 'When' step via Command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context setup handled in the 'When' step via Command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            // Scenario 1: Valid command
            Command cmd = new RenderScreenCmd("screen-1", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_with_null_input() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_fields() {
        try {
            Command cmd = new RenderScreenCmd(null, "3270");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_with_invalid_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bms-fail");
    }

    @When("the RenderScreenCmd command is executed with long device type")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_device_type() {
        try {
            // Scenario 3: Invalid device type (BMS constraint violation: > 4 chars)
            Command cmd = new RenderScreenCmd("screen-bms-fail", "MOBILE-TABLET-PRO");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }
}
