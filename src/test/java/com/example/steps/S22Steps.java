package com.example.steps;

import com.example.domain.navigation.model.FieldDefinition;
import com.example.domain.navigation.model.InputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
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
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Configure a default layout for the valid state
        Map<String, FieldDefinition> layout = new HashMap<>();
        layout.put("CUST_NAME", new FieldDefinition("CUST_NAME", 30, true, ScreenMapAggregate.FieldType.ALPHA));
        layout.put("ACCT_NO", new FieldDefinition("ACCT_NO", 10, true, ScreenMapAggregate.FieldType.NUMERIC));
        
        // No violations
        aggregate.configureForTest(layout, false, false);
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in the 'When' step construction, implies we match the aggregate ID
    }

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Handled in the 'When' step construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_validation() {
        aggregate = new ScreenMapAggregate("SCRN01");
        Map<String, FieldDefinition> layout = new HashMap<>();
        layout.put("CUST_NAME", new FieldDefinition("CUST_NAME", 30, true, ScreenMapAggregate.FieldType.ALPHA));
        
        // Simulate violation: The flag in the aggregate is set to trigger the invariant check
        aggregate.configureForTest(layout, true, false);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("SCRN01");
        Map<String, FieldDefinition> layout = new HashMap<>();
        layout.put("ACCT_NO", new FieldDefinition("ACCT_NO", 10, true, ScreenMapAggregate.FieldType.NUMERIC));
        
        // Simulate violation: The flag in the aggregate is set to trigger the BMS check
        aggregate.configureForTest(layout, false, true);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            Map<String, String> inputs = new HashMap<>();
            inputs.put("CUST_NAME", "John Doe");
            inputs.put("ACCT_NO", "123456");
            
            cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.validatedInput());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
