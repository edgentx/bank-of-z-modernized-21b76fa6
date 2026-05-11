package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {
    
    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        this.screenId = "LOGIN_SCREEN";
    }

    @And("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        this.inputFields = new HashMap<>();
        this.inputFields.put("accountNumber", "12345678");
        this.inputFields.put("amount", "100.00");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenId, inputFields);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof ScreenInputValidatedEvent);
        Assertions.assertEquals("input.validated", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        this.screenId = "LOGIN_SCREEN";
        this.inputFields = new HashMap<>();
        // Missing 'accountNumber'
        this.inputFields.put("amount", "100.00");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        this.screenId = "LOGIN_SCREEN";
        this.inputFields = new HashMap<>();
        // accountNumber max length is 12
        this.inputFields.put("accountNumber", "1234567890123"); 
        this.inputFields.put("amount", "100.00");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
    }
}