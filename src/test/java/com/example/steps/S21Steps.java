package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
        // Assume aggregate is in a valid state for rendering
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid deviceType is provided")
    public void a valid_deviceType_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            Command cmd = new RenderScreenCmd("screen-1", "3270", 80, 24);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-bad");
    }

    @When("the command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_mandatory_fields() {
        try {
            // screenId is null
            Command cmd = new RenderScreenCmd(null, "3270", 80, 24);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-constraint-violation");
    }

    @When("the command is executed with invalid field lengths")
    public void the_command_is_executed_with_invalid_field_lengths() {
        try {
            // width > 80 for legacy 3270 constraint simulation
            Command cmd = new RenderScreenCmd("screen-constraint-violation", "3270", 100, 24);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }
}
