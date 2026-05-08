package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.screen.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants for validation
    private static final String VALID_SCREEN_ID = "LOGIN_SCREEN_01";
    private static final String VALID_DEVICE_TYPE = "DESKTOP";
    private static final String VALID_MAP_ID = "MAP-001";

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        this.aggregate = new ScreenMapAggregate(VALID_MAP_ID);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Data is prepared for the When step, conceptually.
        // We store the valid ID in a context or just use the constant in the When block.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Same as above.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        executeCommand(VALID_SCREEN_ID, VALID_DEVICE_TYPE);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate(VALID_MAP_ID);
    }

    @When("the RenderScreenCmd command is executed with null screenId")
    public void the_RenderScreenCmd_command_is_executed_with_null_screenId() {
        executeCommand(null, VALID_DEVICE_TYPE);
    }

    @When("the RenderScreenCmd command is executed with blank screenId")
    public void the_RenderScreenCmd_command_is_executed_with_blank_screenId() {
        executeCommand("   ", VALID_DEVICE_TYPE);
    }

    @When("the RenderScreenCmd command is executed with null deviceType")
    public void the_RenderScreenCmd_command_is_executed_with_null_deviceType() {
        executeCommand(VALID_SCREEN_ID, null);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        this.aggregate = new ScreenMapAggregate(VALID_MAP_ID);
    }

    @When("the RenderScreenCmd command is executed with screenId exceeding max length")
    public void the_RenderScreenCmd_command_is_executed_with_excessive_screenId() {
        // BMS Max length is 32
        String longId = "THIS_SCREEN_ID_IS_FAR_TOO_LONG_FOR_BMS";
        executeCommand(longId, VALID_DEVICE_TYPE);
    }

    @When("the RenderScreenCmd command is executed with deviceType exceeding max length")
    public void the_RenderScreenCmd_command_is_executed_with_excessive_deviceType() {
        String longType = "THIS_DEVICE_TYPE_DEFINITION_IS_WAY_TOO_LONG";
        executeCommand(VALID_SCREEN_ID, longType);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalArgumentException, "Should be an IllegalArgumentException (Domain Error)");
    }

    // Helper method
    private void executeCommand(String screenId, String deviceType) {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(VALID_MAP_ID, screenId, deviceType);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }
}