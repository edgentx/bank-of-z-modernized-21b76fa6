package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMap;
import com.example.domain.uinavigation.model.ValidateScreenInputCmd;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMap aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMap("LOGIN_SCREEN_01");
        // Setup: No fields defined (open pass)
        aggregate.configureFields(null);
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled in the 'When' step via the aggregate ID
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Implicitly handled in the 'When' step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Assuming 'valid' means empty inputs for a screen with no constraints
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), Map.of());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("MANDATORY_SCREEN");
        // Define 'user' as mandatory
        aggregate.configureFields(Map.of("user", new ScreenMap.FieldDefinition(true, 10)));
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMap("LENGTH_SCREEN");
        // Define 'account' with max length 5
        aggregate.configureFields(Map.of("account", new ScreenMap.FieldDefinition(false, 5)));
        repository.save(aggregate);
    }

    @When("the ValidateScreenInputCmd command is executed for violation")
    public void the_validate_screen_input_cmd_command_is_executed_for_violation() {
        try {
            Map<String, String> inputs;
            
            // Contextually check which aggregate we are dealing with based on ID
            if (aggregate.id().equals("MANDATORY_SCREEN")) {
                // Missing 'user' field
                inputs = Map.of("password", "secret");
            } else if (aggregate.id().equals("LENGTH_SCREEN")) {
                // 'account' is 20 chars (limit 5)
                inputs = Map.of("account", "12345678901234567890");
            } else {
                inputs = Map.of();
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}