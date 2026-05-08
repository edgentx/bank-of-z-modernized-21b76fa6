package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.model.RenderScreenCmd;
import com.example.domain.ui.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup handled in command execution
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup handled in command execution
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Valid inputs for success scenario
        RenderScreenCmd cmd = new RenderScreenCmd("screen-01", "3270");
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
        assertEquals("screen-01", event.aggregateId());
    }

    // Violation Scenarios
    
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-02");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-03");
    }

    // We hook into the previous @When step for error cases, but we need to inject a command based on the context.
    // However, Cucumber steps usually rely on instance state. We will use a flag or check the aggregate ID to determine flow
    // or simply assume the @When step needs to be smart. For simplicity in this file structure, we assume the step
    // implementation might need branching or separate @When methods. Given the prompt structure, we'll override the behavior
    // based on the aggregate ID.
    
    // Note: In a real runner, we might need distinct @When methods or a data table. 
    // To support the generic @When above for all scenarios, we check which aggregate is active.
    
    // Actually, let's refine the @When to handle the specific context of the 2nd scenario.
    // The Cucumber engine will match the first @When it finds. We will keep the implementation in the first @When method
    // or split them if necessary. Let's split them for clarity and standard Cucumber practice.

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data() {
        Command cmd;
        if ("screen-02".equals(aggregate.id())) {
            // Missing fields
            cmd = new RenderScreenCmd(null, "3270");
        } else if ("screen-03".equals(aggregate.id())) {
            // BMS Constraint violation (e.g. device type too long, assuming legacy 4 char limit for example)
            cmd = new RenderScreenCmd("screen-03", "TOOLONGDEVICE");
        } else {
            return; // Not an error case
        }
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

}
