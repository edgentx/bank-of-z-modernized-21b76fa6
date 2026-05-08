package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMap;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import com.example.domain.userinterface.repository.InMemoryScreenMapRepository;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 */
public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMap aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        String id = "screen-map-1";
        aggregate = new ScreenMap(id);
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup, handled in the When block via command object
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup, handled in the When block via command object
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command for positive scenario
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN_SCREEN", "3270");
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("LOGIN_SCREEN", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.layout());
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("invalid-map-1");
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed with invalid fields")
    public void the_render_screen_cmd_command_is_executed_with_invalid_fields() {
        RenderScreenCmd cmd = new RenderScreenCmd(null, "3270"); // Missing screenId
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(caughtException.getMessage().contains("mandatory"), "Error message should indicate mandatory field violation");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMap("bms-violation-map");
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed with exceeding lengths")
    public void the_render_screen_cmd_command_is_executed_with_exceeding_lengths() {
        String longId = "A".repeat(100); // Exceeds max field length of 80
        RenderScreenCmd cmd = new RenderScreenCmd(longId, "3270");
        executeCommand(cmd);
    }

    private void executeCommand(RenderScreenCmd cmd) {
        try {
            // Reload aggregate from repo to ensure clean state for the test
            aggregate = repository.findById(aggregate.id()).orElseThrow();
            resultEvents = aggregate.execute(cmd);
            // Save to simulate persistence of state
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
