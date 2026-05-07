package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context setup usually combined in the When step for brevity, or stored in context
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context setup
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Create a valid command by default
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "web", Map.of("username", "testUser"));
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("screen-map-1", event.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_All_mandatory_input_fields() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // Create invalid command: blank screenId
        cmd = new RenderScreenCmd("screen-map-1", "", "web", Map.of());
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_Field_lengths() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // Create invalid command: field value > 80 chars
        String longValue = "a".repeat(81);
        cmd = new RenderScreenCmd("screen-map-1", "DETAILS", "mainframe", Map.of("description", longValue));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("cannot be blank") || 
                   caughtException.getMessage().contains("exceeds legacy BMS constraint"));
    }
}
