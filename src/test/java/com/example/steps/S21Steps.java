package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception exception;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_violations_mandatory() {
        aggregate = new ScreenMapAggregate("screen-violate-mandatory");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_violations_length() {
        aggregate = new ScreenMapAggregate("screen-violate-length");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Defer command creation to When block to allow overriding in invalid scenarios
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Defer command creation to When block
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Determine context based on the aggregate state or scenario
            // For simplicity, we default to valid data, tests will override setup if needed
            // However, Cucumber steps run sequentially.
            // We need to distinguish scenarios. 
            // Scenario 1: Valid
            // Scenario 2: Violates mandatory (cmd = nulls)
            // Scenario 3: Violates length (cmd = too long)
            
            // Heuristic: if the aggregate ID contains "mandatory", send nulls.
            // If ID contains "length", send long strings.
            // Otherwise send valid data.
            
            String id = aggregate.id();
            String screenId = "ACCTINQ"; // Valid
            String deviceType = "3270";   // Valid
            
            if (id.contains("mandatory")) {
                screenId = null;
                deviceType = null;
            } else if (id.contains("length")) {
                screenId = "TOO_LONG_SCREEN_ID";
                deviceType = "TOO_LONG_DEVICE_TYPE";
            }

            cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType, Map.of());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(exception, "Should not throw exception: " + (exception != null ? exception.getMessage() : ""));
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(exception, "Expected exception but command succeeded");
        assertTrue(exception instanceof IllegalArgumentException || exception instanceof UnknownCommandException);
    }
}
