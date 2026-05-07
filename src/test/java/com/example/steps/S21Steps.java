package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenAggregate aggregate;
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in context of the command execution
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in context of the command execution
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_input() {
        this.aggregate = new ScreenAggregate("screen-invalid");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        this.aggregate = new ScreenAggregate("screen-long");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        Command cmd;
        try {
            // Attempting to execute with a command that might violate constraints based on the setup
            // For the sake of the test, we construct the command inline based on the 'Given' state.
            // In a real scenario, context might be passed differently.
            String id = aggregate.getId().contains("invalid") ? "" : aggregate.getId();
            String type = aggregate.getId().contains("long") ? "MOBILE_EXCESSIVE_LENGTH_DEVICE_TYPE" : "WEB";
            
            cmd = new RenderScreenCmd(id, type);
            this.resultEvents = aggregate.execute(cmd);
            this.caughtException = null;
        } catch (Exception e) {
            this.caughtException = e;
            this.resultEvents = null;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        assertTrue(resultEvents.iterator().next() instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
