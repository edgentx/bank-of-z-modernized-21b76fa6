package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("ACCOUNT_NUM", 10);
        constraints.put("TRANS_AMT", 12);
        List<String> mandatory = List.of("ACCOUNT_NUM", "TRANS_AMT");
        
        this.aggregate = new ScreenMapAggregate("SCRN_001", constraints, mandatory);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Implicitly handled by the aggregate setup, but we verify state
        assertNotNull(aggregate.id());
    }

    @Given("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Placeholder: Input construction happens in the 'When' step for specific scenarios
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        // Default valid input for the happy path
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890");
        inputs.put("TRANS_AMT", "100.00");
        
        executeCommand(inputs);
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals("SCRN_001", event.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        Map<String, Integer> constraints = new HashMap<>();
        List<String> mandatory = List.of("ACCOUNT_NUM", "TRANS_AMT");
        
        this.aggregate = new ScreenMapAggregate("SCRN_002", constraints, mandatory);
    }

    @When("the ValidateScreenInputCmd command is executed for missing mandatory")
    public void the_command_is_executed_for_missing_mandatory() {
        // Missing TRANS_AMT
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890");
        
        executeCommand(inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_length_constraints() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("ACCOUNT_NUM", 10); // Max 10 chars
        List<String> mandatory = List.of("ACCOUNT_NUM");
        
        this.aggregate = new ScreenMapAggregate("SCRN_003", constraints, mandatory);
    }

    @When("the ValidateScreenInputCmd command is executed for length violation")
    public void the_command_is_executed_for_length_violation() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890123"); // 13 chars > 10
        
        executeCommand(inputs);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    private void executeCommand(Map<String, String> inputs) {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}
