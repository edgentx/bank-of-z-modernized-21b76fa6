package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCR-LOGIN-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // State is initialized in aggregate constructor
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Inputs provided in the When step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        // Default valid input for the success scenario
        Map<String, String> validInputs = Map.of("USERID", "ALICE", "PASSWORD", "SECRET");
        executeCommand(new ValidateScreenInputCmd("SCR-LOGIN-01", validInputs));
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void the_validate_screen_input_cmd_command_is_executed_with_missing_mandatory_fields() {
        Map<String, String> invalidInputs = Map.of("USERID", "ALICE"); // Missing PASSWORD
        executeCommand(new ValidateScreenInputCmd("SCR-LOGIN-01", invalidInputs));
    }

    @When("the ValidateScreenInputCmd command is executed with field length violations")
    public void the_validate_screen_input_cmd_command_is_executed_with_field_length_violations() {
        Map<String, String> invalidInputs = Map.of("USERID", "WAY_TOO_LONG_USER_ID");
        executeCommand(new ValidateScreenInputCmd("SCR-LOGIN-01", invalidInputs));
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Exception should be thrown for invalid input");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException, "Should be an IllegalArgumentException");
    }
}