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
        this.aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // In this pattern, screenId is part of the aggregate constructor.
        // If the command needs a different ID, we would set it here.
        // Assuming command targets the existing aggregate for this story context.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Setup handled in the 'When' step construction
    }

    @And("a valid deviceType is provided")
    public void and_a_valid_device_type_is_provided() {
        // Placeholder for scenario flow consistency
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Assuming standard device type like "3270" or "WEB"
            this.command = new RenderScreenCmd(aggregate.id(), "3270");
            this.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertTrue(event instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent rendered = (ScreenRenderedEvent) event;
        assertEquals(aggregate.id(), rendered.aggregateId());
        assertEquals("3270", rendered.deviceType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-bad-mandatory");
        // Simulate the domain state where mandatory fields are invalid
        aggregate.markMandatoryFieldsViolation();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        this.aggregate = new ScreenMapAggregate("screen-bad-bms");
        // Simulate the domain state where BMS constraints are violated
        aggregate.markBmsConstraintViolation();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
