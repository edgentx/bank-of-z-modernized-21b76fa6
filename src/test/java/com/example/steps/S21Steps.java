package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // We defer command creation to When, but store valid params if needed
        // For simplicity, we construct the full command in the When step based on context
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Same as above
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        the_RenderScreenCmd_command_is_executed_with("MAIN_MENU", "3270");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @When("the RenderScreenCmd command is executed with missing screenId")
    public void execute_with_missing_screenId() {
        try {
            command = new RenderScreenCmd(null, "3270", null);
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @When("the RenderScreenCmd command is executed with invalid field length")
    public void execute_with_invalid_length() {
        try {
            // Screen ID exceeding 80 chars
            String tooLongScreenId = "A".repeat(81);
            command = new RenderScreenCmd(tooLongScreenId, "3270", null);
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
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

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        // The error message comes from the domain logic
        assertTrue(thrownException.getMessage().contains("mandatory") || thrownException.getMessage().contains("BMS constraint"));
    }

    // Helper for the successful scenario
    private void the_RenderScreenCmd_command_is_executed_with(String screenId, String deviceType) {
        try {
            command = new RenderScreenCmd(screenId, deviceType, null);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
