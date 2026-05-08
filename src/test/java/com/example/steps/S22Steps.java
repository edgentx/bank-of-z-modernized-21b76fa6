package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
        aggregate = new ScreenMapAggregate("SCREEN1");
        // Configure standard valid fields for the happy path
        aggregate.configureFields(Map.of(
            "ACCOUNT", new ScreenMapAggregate.FieldDefinition(true, 10),
            "AMOUNT", new ScreenMapAggregate.FieldDefinition(true, 12)
        ));
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in context setup, but we ensure cmd uses the correct ID
    }

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Handled in context setup
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            // Default happy path command construction
            if (cmd == null) {
                cmd = new ValidateScreenInputCmd("SCREEN1", Map.of(
                    "ACCOUNT", "1234567890",
                    "AMOUNT", "100.00"
                ));
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("input.validated", resultEvents.get(0).type());
        assertNull(thrownException);
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_All_mandatory_input_fields_must_be_validated() {
        aggregate = new ScreenMapAggregate("SCREEN2");
        aggregate.configureFields(Map.of(
            "TX_ID", new ScreenMapAggregate.FieldDefinition(true, 15) // Mandatory
        ));
        // Construct command missing the mandatory field
        cmd = new ValidateScreenInputCmd("SCREEN2", Map.of()); // Empty input
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_Field_lengths_must_strictly_adhere() {
        aggregate = new ScreenMapAggregate("SCREEN3");
        aggregate.configureFields(Map.of(
            "SHORT_FIELD", new ScreenMapAggregate.FieldDefinition(false, 5) // Max length 5
        ));
        // Construct command exceeding length
        cmd = new ValidateScreenInputCmd("SCREEN3", Map.of(
            "SHORT_FIELD", "TOO_LONG_TEXT" // Length > 5
        ));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("validation failed"));
        assertNull(resultEvents);
    }
}
