package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMap;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMap aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMap("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Defer full cmd creation to 'When' or merge here
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Defer full cmd creation
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("screen-map-bad");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMap("screen-map-len");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            // Default valid values for successful path, overridden if context implies failure
            // (In a real framework, we might use a context table, but simple branching works here)
            String id = (aggregate != null && aggregate.id().equals("screen-map-bad")) ? null : "LOGIN";
            String type = (aggregate != null && aggregate.id().equals("screen-map-bad")) ? null : "3270";
            
            // Simulate BMS violation based on aggregate ID or specific step context flag
            // For the BMS violation scenario:
            if (aggregate != null && aggregate.id().equals("screen-map-len")) {
                id = "VERY_LONG_SCREEN_NAME"; // > 8 chars
                type = "3270";
            } else if (aggregate != null && !aggregate.id().equals("screen-map-bad") && !aggregate.id().equals("screen-map-len")) {
                // Standard Success Case
                id = "LOGIN";
                type = "3270";
            }

            cmd = new RenderScreenCmd(aggregate.id(), id, type, Map.of("user", "test"));
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
