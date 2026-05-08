package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in When construction for context, but we can setup state here if needed.
        // For this flow, we construct the command dynamically in the When step or pass valid defaults.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in When construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command
        executeCommand("LOGIN_SCR_01", "3270");
    }

    @When("the RenderScreenCmd command is executed with screenId={string} and deviceType={string}")
    public void the_render_screen_cmd_command_is_executed_with(String screenId, String deviceType) {
        executeCommand(screenId, deviceType);
    }

    private void executeCommand(String screenId, String deviceType) {
        try {
            // Assuming command is executed against the aggregate which holds its own ID,
            // or the command contains the aggregate ID.
            // Following the pattern of other aggregates in the repo (CustomerEnrolledCmd takes customerId),
            // we pass the aggregate ID to the command.
            command = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals(aggregate.id(), renderedEvent.aggregateId());
        assertNotNull(renderedEvent.occurredAt());
    }

    // --- Scenarios for Rejection ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-violation-001");
        // The violation is defined by the inputs passed in the 'When' step (e.g. nulls)
    }

    @When("the command is executed with invalid data nulls")
    public void the_command_is_executed_with_invalid_data() {
        the_render_screen_cmd_command_is_executed_with(null, "3270");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("map-bms-001");
    }

    @When("the command is executed with screenId {string}")
    public void the_command_is_executed_with_screen_id(String screenId) {
        // Passing a very long screen ID to violate BMS constraints (>32 chars)
        the_render_screen_cmd_command_is_executed_with(screenId, "3270");
    }

}
