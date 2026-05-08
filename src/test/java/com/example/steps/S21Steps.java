package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-MAP-1");
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Stored in context, used in command construction
        // We'll define a standard valid ID here for the happy path
        // But allow override in specific violation scenarios if needed
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Stored in context
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        // The aggregate itself is valid, but the command we construct next will be invalid
        aggregate = new ScreenMapAggregate("SCREEN-MAP-INVALID-INPUT");
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        // The aggregate is valid, but the command will exceed BMS length constraints
        aggregate = new ScreenMapAggregate("SCREEN-MAP-BMS-ERR");
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Construct command based on scenario state.
            // For simplicity in this BDD, we infer the intent from the scenario context or setup defaults.
            // In a real framework, we might parse scenario data tables.
            
            String screenId = "ACCTSUMM"; // Default valid ID
            String deviceType = "3270";     // Default valid device

            // Heuristic check for specific violation scenarios to trigger the error
            // Note: In a rigorous setup, we'd use scenario-specific data injection.
            // Given the constraints, we look at the aggregate ID to decide which command to build.
            if (aggregate.id().equals("SCREEN-MAP-INVALID-INPUT")) {
                screenId = ""; // Violate mandatory
            } else if (aggregate.id().equals("SCREEN-MAP-BMS-ERR")) {
                screenId = "VERY_LONG_SCREEN_ID"; // Violate BMS length (8 chars)
            }

            cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Expected ScreenRenderedEvent");
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception (domain error) to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException, 
            "Expected domain error (IllegalArgumentException or IllegalStateException)");
    }
}
