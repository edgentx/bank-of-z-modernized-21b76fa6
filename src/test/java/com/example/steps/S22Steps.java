package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private Exception caughtException;
    private List<?> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Setup a simple map: Field1 (len 5, mandatory), Field2 (len 10, optional)
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("Field1", 5);
        constraints.put("Field2", 10);
        List<String> mandatory = List.of("Field1");
        
        aggregate = new ScreenMapAggregate("SCREEN-01", constraints, mandatory);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Command setup is mostly done in the inputFields step, 
        // but we ensure context here if needed.
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("Field1", "ABCD"); // Valid length (<5) and not empty
        inputs.put("Field2", "123456789"); // Valid length (<10)
        this.cmd = new ValidateScreenInputCmd("SCREEN-01", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("Field1", 5);
        List<String> mandatory = List.of("Field1");
        
        aggregate = new ScreenMapAggregate("SCREEN-02", constraints, mandatory);
        
        // Input missing Field1
        Map<String, String> inputs = new HashMap<>(); // Empty input
        this.cmd = new ValidateScreenInputCmd("SCREEN-02", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLengthConstraints() {
        Map<String, Integer> constraints = new HashMap<>();
        constraints.put("Field1", 5); // Max length 5
        List<String> mandatory = List.of();
        
        aggregate = new ScreenMapAggregate("SCREEN-03", constraints, mandatory);
        
        // Input exceeds length
        Map<String, String> inputs = new HashMap<>();
        inputs.put("Field1", "TOOLONGTEXT"); // Length > 5
        this.cmd = new ValidateScreenInputCmd("SCREEN-03", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException, "Should be IllegalArgumentException");
    }
}
