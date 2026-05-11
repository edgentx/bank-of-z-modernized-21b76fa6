package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        this.screenId = "LOGIN_SCREEN";
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        this.inputFields = Map.of("USER", "alice", "PASS", "secret");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-123");
        // Violating rule: Empty input fields
        this.inputFields = Map.of();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-123");
        // Violating rule: Field length > 50
        this.inputFields = Map.of("LONG_FIELD", "x".repeat(51));
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenId, inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals("screen-123", event.aggregateId());
        assertEquals("LOGIN_SCREEN", event.screenId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
    }
}
