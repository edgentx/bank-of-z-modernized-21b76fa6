package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import com.example.domain.userinterface.model.InputValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        // Assume standard BMS fields are pre-configured or default
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context: Handled in the 'When' step via command construction
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Context: Handled in the 'When' step via command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Valid command: Screen ID matches aggregate ID, fields are within limits
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                "SCREEN-001", 
                Map.of("FIELD1", "value1", "FIELD2", "value2")
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        // Assuming FIELD1 is mandatory by business rule in the aggregate
        // But we submit empty/null for it
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                "SCREEN-002", 
                Map.of("OPTIONAL_FIELD", "val", "FIELD1", "") // FIELD1 is empty
            );
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        try {
            // Assuming legacy BMS max length for FIELD1 is 10
            String longValue = "0123456789_EXCEEDS"; 
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                "SCREEN-003", 
                Map.of("FIELD1", longValue)
            );
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // Reusing the When step above for error cases

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
