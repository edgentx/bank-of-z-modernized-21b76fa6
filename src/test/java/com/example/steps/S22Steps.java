package com.example.steps;

import com.example.domain.routing.model.ScreenMap;
import com.example.domain.routing.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    // Test Context
    private ScreenMap aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String currentScreenId;
    private Map<String, String> currentInput;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        // Setup a basic map with reasonable constraints
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("accountNum", 12);
        constraints.put("amount", 10);
        constraints.put("reference", 20);

        // 'accountNum' is mandatory
        aggregate = new ScreenMap("map-1", "CUSTINQ", constraints, List.of("accountNum"));
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        currentScreenId = "CUSTINQ01";
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        currentInput = new HashMap<>();
        currentInput.put("accountNum", "123456789");
        currentInput.put("amount", "100.00");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        // This setup matches the valid one, but the When step will provide invalid inputs
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("accountNum", 12);
        // 'accountNum' is mandatory
        aggregate = new ScreenMap("map-2", "CUSTINQ", constraints, List.of("accountNum"));
        currentScreenId = "CUSTINQ01";
        
        // Missing mandatory field
        currentInput = new HashMap<>();
        currentInput.put("amount", "100.00"); // accountNum is missing
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_lengths() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("accountNum", 12); // Max 12 chars
        // No mandatory fields for this specific check, or we provide them
        aggregate = new ScreenMap("map-3", "CUSTINQ", constraints, List.of());
        currentScreenId = "CUSTINQ01";

        currentInput = new HashMap<>();
        currentInput.put("accountNum", "1234567890123"); // 13 chars > 12
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(currentScreenId, currentInput);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("input.validated", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
