package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.*;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.domain.userinterface.repository.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMap aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMap(id);
        // Persist to simulate lifecycle
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // State managed via context hash or implicit setup in the 'When' clause
        // For this BDD style, we often set parameters in the When or context map.
        // Here we ensure the 'valid' context is ready for the 'When'
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context preparation
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default valid data for the happy path
        executeCommand("LOGIN_SCR", "3270");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMap(id);
        repository.save(aggregate);
        // The violation will be in the command executed next
    }

    @When("the RenderScreenCmd command is executed with invalid inputs")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_inputs() {
        // Missing screenId (null)
        executeCommand(null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMap(id);
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed with BMS violating lengths")
    public void the_RenderScreenCmd_command_is_executed_with_BMS_violating_lengths() {
        // screenId > 7 chars (BMS constraint)
        executeCommand("VERY_LONG_SCREEN_ID", "3270");
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        assertEquals("screen.rendered", resultEvents.get(0).type());
        assertNull(caughtException, "Expected no error, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }

    private void executeCommand(String screenId, String deviceType) {
        try {
            // Re-load aggregate to ensure clean state if needed, though in-memory is direct
            if (aggregate == null) {
                // If setup wasn't explicit
                aggregate = new ScreenMap(UUID.randomUUID().toString());
            }
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
