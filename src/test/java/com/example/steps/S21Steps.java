package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        // ID is arbitrary for this context, using a fixed string
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // We defer command creation to 'When' to combine with deviceType
        // Just a marker step to imply state setup
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // We defer command creation to 'When' to combine with screenId
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default valid case data
        executeCommandWith("LOGIN_SCR", "3270_TERMINAL");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-bad-1");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_fields() {
        // Scenario: RenderScreenCmd rejected - All mandatory input fields
        // We override the generic When step here by calling the implementation with bad data
        executeCommandWith(null, "DEVICE_A");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-bad-2");
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_lengths() {
        // Scenario: RenderScreenCmd rejected - Field lengths
        // screenId limit is 8 chars. We send 9.
        executeCommandWith("TOO_LONG_ID", "DEVICE_A");
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("3270_TERMINAL", event.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
    }

    private void executeCommandWith(String screenId, String deviceType) {
        try {
            // Note: The When steps in Gherkin are generic, so we map specific logic here.
            // Cucumber doesn't support overloading step definitions easily based on context,
            // so we construct the specific command based on the 'Given' setup.
            
            // If the aggregate ID indicates the specific violation scenario, we adjust inputs
            if (aggregate.id().equals("screen-map-bad-1")) {
                command = new RenderScreenCmd(null, "DEVICE_A");
            } else if (aggregate.id().equals("screen-map-bad-2")) {
                command = new RenderScreenCmd("TOO_LONG_ID", "DEVICE_A");
            } else {
                command = new RenderScreenCmd(screenId, deviceType);
            }
            
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
