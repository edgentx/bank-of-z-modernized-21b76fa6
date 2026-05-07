package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context usually loaded via constructor or setup
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context usually loaded via constructor or setup
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd("screen-1", DeviceType.DESKTOP, 80, 25);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen-1", event.aggregateId());
        Assertions.assertEquals("application/vnd.banksz.screen+json", event.layoutContentType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-bad-mandatory");
    }

    @When("the command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_mandatory_fields() {
        RenderScreenCmd cmd = new RenderScreenCmd("screen-bad-mandatory", null, 80, 25);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error for mandatory fields")
    public void the_command_is_rejected_with_a_domain_error_for_mandatory_fields() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
        Assertions.assertTrue(capturedException.getMessage().contains("deviceType required"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bad-bms");
    }

    @When("the command is executed with invalid BMS field lengths")
    public void the_command_is_executed_with_invalid_bms_field_lengths() {
        // Legacy 3270 constraint violated: max width 80, max depth 43 (or 24/25 for standard)
        RenderScreenCmd cmd = new RenderScreenCmd("screen-bad-bms", DeviceType.TN3270, 81, 25);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error for BMS constraints")
    public void the_command_is_rejected_with_a_domain_error_for_bms_constraints() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
        Assertions.assertTrue(capturedException.getMessage().contains("width must be"));
    }
}
