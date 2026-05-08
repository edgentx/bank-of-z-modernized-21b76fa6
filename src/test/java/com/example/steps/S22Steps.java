package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.ScreenInputValidatedEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("TEST_SCR_01");
        aggregate.defineField("ACCOUNT_NUM", 12, true);
        aggregate.defineField("TRANS_AMT", 10, true);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicit in the aggregate initialization, used when constructing the cmd
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Inputs provided in the 'When' step construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        executeWithValidInput();
    }

    private void executeWithValidInput() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "123456789");
        inputs.put("TRANS_AMT", "100.00");
        
        Command cmd = new ValidateScreenInputCmd("TEST_SCR_01", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_rules() {
        aggregate = new ScreenMapAggregate("TEST_SCR_02");
        aggregate.defineField("REF_NUM", 10, true);
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_fields() {
        Map<String, String> inputs = new HashMap<>(); // Missing REF_NUM
        Command cmd = new ValidateScreenInputCmd("TEST_SCR_02", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_length_constraints() {
        aggregate = new ScreenMapAggregate("TEST_SCR_03");
        aggregate.defineField("SHORT_CODE", 5, true);
    }

    @When("the ValidateScreenInputCmd command is executed with exceeding length")
    public void the_command_is_executed_with_exceeding_length() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("SHORT_CODE", "123456"); // Length 6 > Limit 5
        Command cmd = new ValidateScreenInputCmd("TEST_SCR_03", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}