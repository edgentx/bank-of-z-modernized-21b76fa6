package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ValidateScreenInputCmd;
import com.example.domain.userinterfacenavigation.model.ScreenInputValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        // Define some fields to make it valid/realistic for testing
        aggregate.defineField("USER_ID", 10, true);
        aggregate.defineField("PASSWORD", 20, true);
        aggregate.defineField("OPTIONAL_DATA", 30, false);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in context setup, ensuring command matches aggregate ID
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        Map<String, String> fields = new HashMap<>();
        fields.put("USER_ID", "ALICE");
        fields.put("PASSWORD", "secret");
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", fields);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_mandatory_field_violation() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 10, true); // Mandatory
        aggregate.defineField("PASSWORD", 20, true); // Mandatory

        // Input missing PASSWORD
        Map<String, String> fields = new HashMap<>();
        fields.put("USER_ID", "BOB");
        // Missing PASSWORD
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", fields);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_length_violation() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 5, true); // Max length 5
        aggregate.defineField("PASSWORD", 20, true);

        Map<String, String> fields = new HashMap<>();
        fields.put("USER_ID", "CHARLIE_TOO_LONG"); // Length 16 > 5
        fields.put("PASSWORD", "secret");
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("LOGIN_SCREEN", event.aggregateId());
        assertEquals("userinterfacenavigation.input.validated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        
        // Verify specific message content based on invariants
        String msg = caughtException.getMessage();
        assertTrue(msg.contains("mandatory") || msg.contains("BMS") || msg.contains("constraints"));
    }
}
