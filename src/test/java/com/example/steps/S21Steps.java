package com.example.steps;

import com.example.domain.ScreenMapAggregate;
import com.example.domain.RenderScreenCmd;
import com.example.domain.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in When clause construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in When clause construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-invalid-mandatory");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_lengths() {
        this.aggregate = new ScreenMapAggregate("screen-invalid-length");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Determine command payload based on context (simplified for this step file)
            if (aggregate.id().equals("screen-invalid-mandatory")) {
                 this.command = new RenderScreenCmd("screen-invalid-mandatory", null, "3270");
            } else if (aggregate.id().equals("screen-invalid-length")) {
                 // 80 chars is max for 3270 model 2/4/5, assuming 79 is safe, 80 is fail or 81 fail
                 this.command = new RenderScreenCmd("screen-invalid-length", "SCREEN_ID_WAY_TOO_LONG_FOR_BMS", "3270");
            } else {
                 this.command = new RenderScreenCmd("screen-1", "MAIN_MENU", "WEB");
            }

            this.resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.caughtException = e;
        } catch (UnknownCommandException e) {
            this.caughtException = e;
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
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
