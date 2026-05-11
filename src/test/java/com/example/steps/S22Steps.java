package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cucumber Steps for S-22: ValidateScreenInputCmd.
 */
public class S22Steps {

    private ScreenMapAggregate aggregate;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
        // Setup basic rules for this screen map
        Map<String, Integer> lengths = new HashMap<>();
        lengths.put("accountNumber", 10);
        lengths.put("amount", 12);
        
        Set<String> mandatory = Set.of("accountNumber", "amount");
        
        aggregate.applyRules(lengths, mandatory);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-strict");
        aggregate.applyRules(Map.of("accountNumber", 10), Set.of("accountNumber"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-bms");
        // Legacy constraint: accountNumber must be <= 10
        aggregate.applyRules(Map.of("accountNumber", 10), Set.of());
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // The screenId is implicitly provided via the aggregate creation in the Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        inputFields = new HashMap<>();
        inputFields.put("accountNumber", "1234567890");
        inputFields.put("amount", "100.00");
    }

    @And("an invalid inputFields is provided missing mandatory fields")
    public void an_invalid_inputFields_is_provided_missing_mandatory() {
        inputFields = new HashMap<>();
        // Missing 'accountNumber' which is mandatory
        inputFields.put("amount", "100.00");
    }

    @And("an invalid inputFields is provided exceeding field lengths")
    public void an_invalid_inputFields_is_provided_exceeding_lengths() {
        inputFields = new HashMap<>();
        // Exceeds length of 10 defined in the Given step
        inputFields.put("accountNumber", "123456789012345");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            // In the 'violates mandatory' scenario, we need to inject the bad data context
            if (aggregate.getId().equals("screen-strict")) {
                 an_invalid_inputFields_is_provided_missing_mandatory();
            } else if (aggregate.getId().equals("screen-bms")) {
                 an_invalid_inputFields_is_provided_exceeding_lengths();
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), inputFields);
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
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("Validation failed"));
    }
}