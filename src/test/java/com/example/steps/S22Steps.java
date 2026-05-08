package com.example.steps;

import com.example.domain.screenmap.model.*;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<ScreenInputValidatedEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("SCR-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context handled in the 'When' step construction
    }

    @Given("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Context handled in the 'When' step construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        // Default valid inputs
        Command cmd = new ValidateScreenInputCmd("SCR-01", Map.of("USER_ID", "12345"));
        try {
            var events = aggregate.execute(cmd);
            // Filter for the specific event type if needed, or just check list size
            resultEvents = events.stream()
                    .filter(e -> e instanceof ScreenInputValidatedEvent)
                    .map(e -> (ScreenInputValidatedEvent) e)
                    .toList();
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCR-02");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("SCR-03");
    }
}
