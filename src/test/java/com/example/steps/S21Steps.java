package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import com.example.domain.userinterfacenavigation.command.RenderScreenCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {
    
    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // In-memory repository logic handled via direct aggregate instantiation for this test scope
    // or via a mock if using the repository pattern strictly. Here we instantiate directly.

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the When block via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the When block via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            Command cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "3270");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("LOGIN_SCREEN", event.screenId());
        Assertions.assertEquals("3270", event.deviceType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_input_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-invalid");
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data() {
        try {
            // screenId is blank, violating the mandatory rule
            Command cmd = new RenderScreenCmd("screen-map-invalid", "", "3270");
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        this.aggregate = new ScreenMapAggregate("screen-map-bms");
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        try {
            // screenId > 80 chars violates BMS constraint (assuming 80 max for this example)
            String longId = "VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_THE_LEGACY_BMS_BUFFER_LIMIT_ALLOWED_FOR_THIS_FIELD_TYPE_IN_SYSTEM_Z";
            Command cmd = new RenderScreenCmd("screen-map-bms", longId, "3270");
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

}
