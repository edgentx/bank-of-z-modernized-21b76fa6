package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.screen.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to reset state
    private void createAggregate(String id) {
        this.aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        createAggregate("map-001");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup happens in the 'When' step or via specific context variables
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup happens in the 'When' step
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        createAggregate("map-error-001");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSConstraints() {
        createAggregate("map-error-002");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Determine scenario context based on aggregate ID to know what command to construct
        // This is a simplification; in a real test we might use scenario context variables
        RenderScreenCmd cmd;
        
        if (aggregate.id().equals("map-001")) {
            // Happy Path
            cmd = new RenderScreenCmd(aggregate.id(), "LOGIN_SCR", "DESKTOP", Map.of("username", "testuser"));
        } else if (aggregate.id().equals("map-error-001")) {
            // Validation Error: Missing screenId
            cmd = new RenderScreenCmd(aggregate.id(), "", "DESKTOP", Map.of());
        } else if (aggregate.id().equals("map-error-002")) {
            // Validation Error: BMS Length Constraint
            String longString = "x".repeat(100); // Exceeds MAX_FIELD_LENGTH (80)
            cmd = new RenderScreenCmd(aggregate.id(), "LOGIN_SCR", "3270", Map.of("longField", longString));
        } else {
            cmd = new RenderScreenCmd(aggregate.id(), "UNKNOWN", "UNKNOWN", Map.of());
        }

        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
