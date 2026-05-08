package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.HashMap;

public class S22Steps {

    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
        // Simulating a valid screen map configuration directly for the test
        aggregate.configureMap(
            Map.of("field1", 10, true),
            Map.of("field1", 10)
        );
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // No-op: screenId 'screen-1' is set in the aggregate
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // No-op: inputs provided in When step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("field1", "value");
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("screen-1", inputs);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        var events = aggregate.uncommittedEvents();
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof InputValidatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-2");
        // 'field1' is mandatory (true)
        aggregate.configureMap(
            Map.of("field1", 10, true),
            Map.of("field1", 10)
        );
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-3");
        // 'field1' max length is 5
        aggregate.configureMap(
            Map.of("field1", 5, false),
            Map.of("field1", 5)
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Map<String, String> inputs = new HashMap<>();
        
        // Specific data setup based on the context
        if (aggregate.id().equals("screen-2")) {
            // Missing mandatory field
            inputs.put("someOtherField", "val");
        } else if (aggregate.id().equals("screen-3")) {
            // Field too long
            inputs.put("field1", "123456"); // length 6 > 5
        }
        
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
        
        Exception ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        Assertions.assertTrue(ex.getMessage().contains("validation") || ex.getMessage().toLowerCase().contains("required") || ex.getMessage().toLowerCase().contains("length"));
    }
}
