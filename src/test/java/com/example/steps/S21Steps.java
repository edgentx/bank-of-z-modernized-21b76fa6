package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private InMemoryScreenMapRepository repository;
    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    // Helper to reset state for each scenario
    private void init() {
        repository = new InMemoryScreenMapRepository();
        aggregate = null;
        resultingEvents = null;
        capturedException = null;
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        init();
        String id = "screen-map-001";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // No-op - the command construction uses valid defaults in the 'When' step.
        // Or we can set a context variable if needed.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // No-op - the command construction uses valid defaults in the 'When' step.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Constructing a valid command for the happy path
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-001", "LOGIN", "web", "BMSLOG01");
            // Reload aggregate to simulate persistence fetch
            ScreenMapAggregate agg = repository.findById(cmd.screenMapId());
            resultingEvents = agg.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        init();
        // The violation occurs in the Command data, not the aggregate state itself for this specific invariant.
        // We create a valid aggregate, but will pass an invalid command in the 'When' step.
        String id = "screen-map-002";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed for mandatory check")
    public void theRenderScreenCmdCommandIsExecutedForMandatoryCheck() {
        try {
            // Constructing an INVALID command (null screenId)
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-002", null, "mobile", "BMSACC02");
            ScreenMapAggregate agg = repository.findById(cmd.screenMapId());
            resultingEvents = agg.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        init();
        String id = "screen-map-003";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed for field length check")
    public void theRenderScreenCmdCommandIsExecutedForFieldLengthCheck() {
        try {
            // Constructing an INVALID command (screenId too long for BMS)
            // Legacy constraint: 8 chars max.
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-003", "VERY_LONG_SCREEN_NAME", "desktop", "BMSTAB03");
            ScreenMapAggregate agg = repository.findById(cmd.screenMapId());
            resultingEvents = agg.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // Generic Then for rejection
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // In this domain model, validation errors manifest as IllegalArgumentExceptions
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertNull(resultingEvents);
    }

}
