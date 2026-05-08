package com.example.steps;

import com.example.domain.screen.model.InputValidatedEvent;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        List<ScreenMapAggregate.FieldDefinition> fields = List.of(
            new ScreenMapAggregate.FieldDefinition("accountNum", 10, true),
            new ScreenMapAggregate.FieldDefinition("amount", 12, false)
        );
        aggregate = new ScreenMapAggregate("LOGIN_SCR", fields);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the 'When' step construction via the aggregate ID context
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        this.cmd = new ValidateScreenInputCmd("LOGIN_SCR", Map.of("accountNum", "12345"));
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        List<ScreenMapAggregate.FieldDefinition> fields = List.of(
            new ScreenMapAggregate.FieldDefinition("accountNum", 10, true)
        );
        aggregate = new ScreenMapAggregate("LOGIN_SCR", fields);
        // Missing 'accountNum' in input
        this.cmd = new ValidateScreenInputCmd("LOGIN_SCR", Map.of());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        List<ScreenMapAggregate.FieldDefinition> fields = List.of(
            new ScreenMapAggregate.FieldDefinition("shortCode", 5, false)
        );
        aggregate = new ScreenMapAggregate("INPUT_SCR", fields);
        // Input length 6 exceeds max 5
        this.cmd = new ValidateScreenInputCmd("INPUT_SCR", Map.of("shortCode", "123456"));
    }
}