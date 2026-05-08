package com.example.steps;

import com.example.domain.ui.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the execution step via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the execution step via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("screen-1", DeviceType.WEB);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_render_screen_cmd_command_is_executed_with_missing_fields() {
        try {
            // Assuming 'screenId' is mandatory and passed to constructor, 
            // we can test invariant violation by passing null deviceType
            RenderScreenCmd cmd = new RenderScreenCmd("screen-invalid", null);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_bms_length() {
        aggregate = new ScreenMapAggregate("screen-long");
    }

    @When("the RenderScreenCmd command is executed with long fields")
    public void the_render_screen_cmd_command_is_executed_with_long_fields() {
        try {
            // Testing BMS constraint: DeviceType must be strictly <= 4 chars for legacy mapping
            // Use an invalid value that doesn't match the enum but triggers a length check logic if we were passing raw strings
            // Since we use an Enum, the length constraint is implicit, but let's simulate a constraint check on the Aggregate side logic
            // We'll pass a valid Enum object, but assume the command payload might have failed validation earlier or
            // the aggregate enforces a specific constraint (e.g. specific IDs allowed).
            // However, to strictly follow the prompt "Field lengths...", let's assume the Command takes a raw String or the Aggregate validates the Enum's name length.
            
            // Given the structure, I will modify the Command slightly to accept raw String for this specific constraint test,
            // OR assume the Aggregate internally checks the Enum's name length against a legacy limit.
            // Let's stick to the Enum defined in the Command and assume the 'deviceType' length constraint refers to a hypothetical raw input
            // validated by the Aggregate. I will simulate this by passing a valid Enum, but the aggregate checks for a hypothetical 'screenLayout' length.
            // WAIT: The prompt says "Field lengths". I will assume the Command should accept String for deviceType to demonstrate validation,
            // OR keep Enum and throw if string length > X.
            // DECISION: I will keep the Command with Enum. I will inject a hypothetical payload into the command or assume the validation is on the 'screenId' being too long for BMS.
            
            RenderScreenCmd cmd = new RenderScreenCmd("this-screen-id-is-way-too-long-for-bms-buffer", DeviceType.WEB);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

}
