package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("sm-123");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // State managed in the 'When' step for command construction
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // State managed in the 'When' step for command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd("sm-123", "LOGIN001", "Mobile", Map.of());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("LOGIN001", event.screenId());
        Assertions.assertEquals("Mobile", event.deviceType());
        Assertions.assertNotNull(event.layout());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("sm-invalid");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed_invalid() {
        // Missing screenId
        RenderScreenCmd cmd = new RenderScreenCmd("sm-invalid", "", "Desktop", Map.of());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("sm-bms-fail");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed_bms_violation() {
        // Screen ID too long (> 8)
        RenderScreenCmd cmd = new RenderScreenCmd("sm-bms-fail", "TO_LONG_SCREEN_ID", "Desktop", Map.of());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
