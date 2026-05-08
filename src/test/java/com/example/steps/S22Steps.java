package com.example.steps;

import com.example.domain.navigation.model.InputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCRN001");
        // Configure a mock BMS map
        Map<String, Integer> lengths = new HashMap<>();
        lengths.put("ACCOUNT_NO", 10);
        lengths.put("TRANS_CODE", 3);
        lengths.put("AMOUNT", 12);
        
        aggregate.configure(
            "Customer Inquiry",
            lengths,
            List.of("ACCOUNT_NO", "TRANS_CODE") // Mandatory fields
        );
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in constructor of command
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "1234567890"); // Valid length 10
        inputs.put("TRANS_CODE", "201");      // Valid length 3
        inputs.put("AMOUNT", "100.00");        // Valid length 12
        this.cmd = new ValidateScreenInputCmd("SCRN001", inputs);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCRN002");
        // Mark TRANS_CODE as mandatory
        aggregate.configure(
            "Transfer Screen",
            Map.of("ACCOUNT_NO", 10, "TRANS_CODE", 3),
            List.of("ACCOUNT_NO", "TRANS_CODE")
        );
        
        // Input missing TRANS_CODE
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "9999999999");
        // TRANS_CODE is missing
        this.cmd = new ValidateScreenInputCmd("SCRN002", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("SCRN003");
        aggregate.configure(
            "Login Screen",
            Map.of("USER_ID", 8), // Max 8 chars
            List.of("USER_ID")
        );

        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "ADMINISTRATOR"); // Length 13 > 8
        this.cmd = new ValidateScreenInputCmd("SCRN003", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof InputValidatedEvent);
        
        InputValidatedEvent iv = (InputValidatedEvent) event;
        assertEquals("input.validated", iv.type());
        assertEquals("SCRN001", iv.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
