package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import com.example.domain.userinterface.model.InputValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class S22Steps {

    private ScreenMapAggregate screenMap;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        screenMap = new ScreenMapAggregate("SCR-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in command construction below
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Setup command with valid data: Map satisfying the aggregate's mandatory fields (ACCOUNT_NO)
        // Assuming ACCOUNT_NO max length 10 based on typical legacy constraints, and it is mandatory.
        this.cmd = new ValidateScreenInputCmd("SCR-01", Map.of("ACCOUNT_NO", "1234567890"));
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        screenMap = new ScreenMapAggregate("SCR-01");
        // Provide empty map (missing mandatory ACCOUNT_NO)
        this.cmd = new ValidateScreenInputCmd("SCR-01", Map.of());
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        screenMap = new ScreenMapAggregate("SCR-01");
        // Provide ACCOUNT_NO that exceeds max length of 10
        this.cmd = new ValidateScreenInputCmd("SCR-01", Map.of("ACCOUNT_NO", "1234567890123"));
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull("No events were emitted", resultEvents);
        assertEquals("Should emit exactly one event", 1, resultEvents.size());
        assertTrue("Event should be InputValidatedEvent", resultEvents.get(0) instanceof InputValidatedEvent);

        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("screen.validated", event.type());
        assertEquals("SCR-01", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        // Checking for IllegalArgumentException or IllegalStateException as domain error indicators
        assertTrue("Exception should be IllegalArgumentException or IllegalStateException", 
                   caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
        assertNull("No events should be emitted on failure", resultEvents);
    }
}
