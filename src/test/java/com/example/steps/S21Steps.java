package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
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
        aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context setup handled in the When step via a direct valid command
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context setup handled in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        executeCommand(new RenderScreenCmd("screen-map-123", "LOGIN_SCREEN", "DESKTOP"));
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(ScreenRenderedEvent.class, resultEvents.get(0).getClass());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_data() {
        // Pass null screenId to violate validation
        executeCommand(new RenderScreenCmd("screen-map-invalid", null, "MOBILE"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception");
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-bms-fail");
    }

    @When("the RenderScreenCmd command is executed with excessive field length")
    public void the_RenderScreenCmd_command_is_executed_with_excessive_length() {
        // Create a screenId longer than 80 chars
        String longScreenId = "A".repeat(100);
        executeCommand(new RenderScreenCmd("screen-map-bms-fail", longScreenId, "TERMINAL_3270"));
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        } catch (Exception e) {
            capturedException = e;
        }
    }
}