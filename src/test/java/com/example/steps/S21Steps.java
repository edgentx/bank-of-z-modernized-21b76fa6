package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Defer command creation until all parts are ready, or store parts.
        // For simplicity in this step, we assume command construction is deferred or handled in 'When'.
        // However, to fit the flow, we'll store state or construct the cmd now and overwrite if needed.
        // Let's construct a default valid command here.
        cmd = new RenderScreenCmd("ACCTMENU", "3270");
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Update command if parts were set previously, or just ensuring validity.
        // If cmd was null from previous step, create it.
        if (cmd == null) {
            cmd = new RenderScreenCmd("ACCTMENU", "3270");
        } else {
            // Re-instantiate to be sure, though record fields are final.
             cmd = new RenderScreenCmd(cmd.screenId(), "3270");
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-002");
        // Setting up a command with a missing/null field for the scenario
        cmd = new RenderScreenCmd(null, "3270"); // screenId is null
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_legacy_lengths() {
        aggregate = new ScreenMapAggregate("map-003");
        // Legacy BMS names often 8 chars. Let's violate it.
        cmd = new RenderScreenCmd("LONGSCRNAME", "WEB");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
