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
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("sm-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the When clause construction for simplicity in this pattern,
        // or we could store the screenId in a instance variable.
        // For these scenarios, we construct the command with valid data in the 'When' step
        // or specific Given steps.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Same as above
    }

    // --- Scenario 1 ---

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid execution context
        cmd = new RenderScreenCmd("SCRN01", "3270");
        executeCommand();
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("SCRN01", event.screenId());
        assertEquals("3270", event.deviceType());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Scenario 2: Validation ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("sm-2");
        // We set the command with invalid data (nulls) here or in the When.
        // Since Cucumber 'Given' sets context, let's prepare the command state.
        // But the method signature for 'When... executed' is shared.
        // We will use a flag or specific context.
        // However, Cucumber steps are distinct. We can overload or use a context variable.
        // Let's look at the 'When' step. We need a way to pass the 'bad' command.
    }

    // Custom When for Scenario 2
    @When("the RenderScreenCmd command is executed with invalid fields")
    public void the_render_screen_cmd_command_is_executed_with_invalid_fields() {
        cmd = new RenderScreenCmd(null, "3270"); // screenId is null
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory") || 
                   capturedException.getMessage().contains("required"));
    }

    // --- Scenario 3: Legacy Constraints ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("sm-3");
    }

    @When("the RenderScreenCmd command is executed with long fields")
    public void the_render_screen_cmd_command_is_executed_with_long_fields() {
        // Constraint is max 8 chars. "TOOLONGSCREENID" is > 8.
        cmd = new RenderScreenCmd("TOOLONGSCREENID", "3270");
        executeCommand();
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
