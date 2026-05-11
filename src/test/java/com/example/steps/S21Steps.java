package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        String id = "screen-map-1";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in context or assumed valid for happy path
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in context
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid data for the happy path unless modified by specific Given steps
        // In a real Cucumber scenario, we might use a data table or context injection.
        // Here we assume the 'Valid' context implies creating a valid command.
        try {
            // Re-fetching to simulate persistence/load flow if necessary, or using instance
            var agg = repository.findById("screen-map-1").orElseThrow();
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", "DESKTOP");
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Save state
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals("screen-map-1", renderedEvent.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        String id = "screen-map-invalid-mandatory";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
        
        // Override the command execution logic for this specific scenario context
        // Normally we'd parameterize the When step, but to keep simple:
        try {
            var agg = repository.findById(id).orElseThrow();
            // Violation: null screenId
            RenderScreenCmd cmd = new RenderScreenCmd(id, null, "DESKTOP");
            resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        String id = "screen-map-invalid-length";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);

        try {
            var agg = repository.findById(id).orElseThrow();
            // Violation: screenId > 8 chars (assuming constraint based on story)
            RenderScreenCmd cmd = new RenderScreenCmd(id, "VERY_LONG_SCREEN_ID", "DESKTOP");
            resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
