package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.userinterfacenavigation.model.*;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import com.example.domain.userinterfacenavigation.repository.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCR001");
        // Seed state as if it was created/hydrated
        aggregate.applyCreatedEvent(new ScreenMapCreatedEvent("SCR001", "Main Menu", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Assume SCR001 matches the aggregate
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // No-op setup, handled in When step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // For the success case, we assume a map with a mandatory field defined and valid length
            Map<String, String> fields = Map.of("USERNAME", "testuser");
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCR001", fields);
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCR002");
        // Define a screen where 'PASSWORD' is mandatory
        aggregate.applyCreatedEvent(new ScreenMapCreatedEvent("SCR002", "Login", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("SCR003");
        // BMS constraint: USERNAME max 8 chars
        aggregate.applyCreatedEvent(new ScreenMapCreatedEvent("SCR003", "Profile", Instant.now()));
        repository.save(aggregate);
    }

    @When("the ValidateScreenInputCmd command is executed for violation checks")
    public void the_validate_screen_input_cmd_command_is_executed_for_violations() {
        try {
            Map<String, String> fields;
            if (aggregate.id().equals("SCR002")) {
                // Missing mandatory field
                fields = Map.of("USERNAME", "user");
            } else if (aggregate.id().equals("SCR003")) {
                // Exceeds length (BMS limit 8)
                fields = Map.of("USERNAME", "verylongusername");
            } else {
                fields = Map.of();
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), fields);
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof ScreenInputValidatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Helper hook to run Cucumber via JUnit 5 if needed, or rely on standalone
    // This is usually in a separate TestRunner class, but defined here for completeness of the Step file context.
}
