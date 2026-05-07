package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_mandatory_violations() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_field_length_violations() {
        aggregate = new ScreenMapAggregate("screen-map-len-invalid");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Heuristic: Check test context or assume valid defaults if not specified explicitly in Gherkin steps.
        // For the purpose of this exercise, we attempt a command that might fail based on the 'Given' context.
        // In a real world, we might parse the Gherkin table or use scenario context variables.
        // We will try to execute a command that triggers the validation logic.
        try {
            // Assuming the violations scenario implies executing with specific bad data. 
            // Since Cucumber doesn't pass args in this specific phrasing, we simulate the execution.
            // We use a helper to determine which command to dispatch based on the context.
            RenderScreenCmd cmd = determineCommandFromContext();
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    private RenderScreenCmd determineCommandFromContext() {
        // Check specific scenario logic roughly based on variable state or specific IDs used in Given
        if (aggregate.id().equals("screen-map-invalid")) {
            // Missing screenId
            return new RenderScreenCmd("", Map.of("deviceType", "3270"));
        } else if (aggregate.id().equals("screen-map-len-invalid")) {
            // Field length violation (BMS limit usually small, let's say 10 for this test)
            return new RenderScreenCmd("screen-1", Map.of("deviceType", "THIS_DEVICE_TYPE_IS_WAY_TOO_LONG_FOR_BMS"));
        } else {
            // Valid defaults
            return new RenderScreenCmd("LOGIN_SCR_01", Map.of("deviceType", "3270"));
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen-map-1", event.aggregateId());
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
