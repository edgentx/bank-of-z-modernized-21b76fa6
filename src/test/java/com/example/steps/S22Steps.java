package com.example.steps;

import com.example.domain.navigation.model.FieldDefinition;
import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        List<FieldDefinition> fields = List.of(
            new FieldDefinition("ACCOUNT_NUM", 10, true),
            new FieldDefinition("TRANS_AMT", 12, true),
            new FieldDefinition("REF_CODE", 5, false) // Optional
        );
        aggregate = new ScreenMapAggregate("SCR01", fields);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in context of the command creation
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Handled in context of the command creation
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        List<FieldDefinition> fields = List.of(
            new FieldDefinition("ACCOUNT_NUM", 10, true),
            new FieldDefinition("TRANS_AMT", 12, true)
        );
        aggregate = new ScreenMapAggregate("SCR01", fields);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        List<FieldDefinition> fields = List.of(
            new FieldDefinition("ACCOUNT_NUM", 10, true)
        );
        aggregate = new ScreenMapAggregate("SCR01", fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Default valid input for the happy path, modified by specific scenarios if needed
            // Note: In a real framework, we'd inject these from the scenario context.
            // Based on the Gherkin "Given a valid inputFields is provided", we assume valid data for the success case.
            // For the failure cases, we modify input here based on state.
            
            Map<String, String> inputs;
            
            // Heuristic: Check if we are in a 'violation' scenario by looking at the aggregate setup
            // This is a simplification for the generated steps; typically we'd parse the Gherkin table.
            if (aggregate.hasField("ACCOUNT_NUM") && aggregate.hasField("TRANS_AMT")) {
                 // Case: Missing mandatory
                 inputs = Map.of("ACCOUNT_NUM", "12345"); // TRANS_AMT is missing
            } else if (aggregate.hasField("ACCOUNT_NUM") && !aggregate.hasField("TRANS_AMT")) {
                 // Case: Length violation (Account is 10, input is 12)
                 inputs = Map.of("ACCOUNT_NUM", "123456789012"); 
            } else {
                // Default Happy Path
                inputs = Map.of(
                    "ACCOUNT_NUM", "12345",
                    "TRANS_AMT", "100.00",
                    "REF_CODE", "A1"
                );
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCR01", inputs);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals("SCR01", validatedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // The prompt specifies "Domain Error". In Java DDD, this is typically an IllegalStateException
        // or a custom DomainException. Our implementation throws IllegalStateException.
        assertTrue(capturedException instanceof IllegalStateException);
        
        String message = capturedException.getMessage();
        assertTrue(message.contains("mandatory") || message.contains("BMS") || message.contains("length"));
    }
}
