package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private final InMemoryScreenMapRepository repo = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private ScreenInputValidatedEvent lastEvent;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
        repo.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the execution step, implicitly verifying setup
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Handled in the execution step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Setup basic inputs for success scenario
            Map<String, String> inputs = Map.of("ACCOUNT", "12345");
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCRN01", inputs);
            
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (ScreenInputValidatedEvent) events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(lastEvent, "Event should not be null");
        assertEquals("input.validated", lastEvent.type());
        assertEquals("SCRN01", lastEvent.aggregateId());
    }

    // --- Scenarios for Rejection ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCRN02");
        aggregate.setMandatoryFields(Set.of("ACCOUNT", "PIN")); // Both required
        repo.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMapAggregate("SCRN03");
        aggregate.setFieldLengths(Map.of("ACCOUNT", 5)); // Max 5 chars
        repo.save(aggregate);
    }

    @When("the ValidateScreenInputCmd command is executed on the violating aggregate")
    public void the_validate_screen_input_cmd_command_is_executed_on_violating_aggregate() {
        try {
            Map<String, String> inputs;
            
            // Context-specific logic based on aggregate setup in previous step
            if (aggregate.id().equals("SCRN02")) {
                // Missing 'PIN'
                inputs = Map.of("ACCOUNT", "12345");
            } else if (aggregate.id().equals("SCRN03")) {
                // Too long
                inputs = Map.of("ACCOUNT", "1234567890"); 
            } else {
                inputs = Map.of();
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
