package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in context construction
    }

    @Given("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Handled in context construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-missing-mandatory");
        // Setup command with missing field
        cmd = new ValidateScreenInputCmd("screen-missing-mandatory", Map.of("OPTIONAL_FIELD", "data"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-too-long");
        // Setup command with field exceeding length (e.g., 10 chars)
        String longValue = "12345678901";
        cmd = new ValidateScreenInputCmd("screen-too-long", Map.of("SHORT_FIELD", longValue));
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            if (cmd == null) {
                // Default valid command for the happy path
                cmd = new ValidateScreenInputCmd("screen-123", Map.of("MANDATORY_FIELD", "value"));
            }
            aggregate.execute(cmd);
        } catch (DomainException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof InputValidatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof DomainException);
    }
}
