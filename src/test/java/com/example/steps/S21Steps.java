package com.example.steps;

import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-1");
        this.capturedException = null;
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled in the WHEN step by constructing the command with valid data
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Implicitly handled in the WHEN step
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_invalid_inputs() {
        this.aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_lengths() {
        this.aggregate = new ScreenMapAggregate("screen-length-violation");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Contextual Command construction based on the 'Given' state is tricky in pure Cucumber without shared state.
            // However, based on the specific error scenario, we can infer the violation.
            String screenId = "DEFAULT_SCREEN";
            String deviceType = "3270";
            Map<String, String> inputData = new HashMap<>();
            inputData.put("account", "123456");

            // Adjusting data for specific negative scenarios based on aggregate ID
            if (aggregate.id().equals("screen-invalid")) {
                inputData.put("mandatory_field", null); // Trigger mandatory validation
            } else if (aggregate.id().equals("screen-length-violation")) {
                 inputData.put("long_field", "THIS_VALUE_IS_TOO_LONG_FOR_BMS_MAP"); // Trigger length validation
            }

            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType, inputData);
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
