package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.navigation.repository.InMemoryScreenMapRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final InMemoryScreenMapRepository repo = new InMemoryScreenMapRepository();
    private Throwable thrownException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-001");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingFields() {
        this.aggregate = new ScreenMapAggregate("screen-error-001");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithInvalidBmsLengths() {
        this.aggregate = new ScreenMapAggregate("screen-bms-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup, ID is part of the aggregate construction, or passed via command
        // For this scenario, we assume the command uses the aggregate's ID or the ID provided in context.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command values for the happy path
        String screenId = (aggregate != null) ? aggregate.id() : "unknown";
        String deviceType = "3270";

        // Specific overrides based on scenario context would strictly be handled via scenario state,
        // but given the step definitions, we map the "Given" context to command inputs.
        // Scenario 1: Valid inputs (defaults)
        // Scenario 2: Violates mandatory fields -> We will simulate this by passing nulls via specific test logic if needed,
        // but the aggregate validation handles the rejection based on its internal state or command content.

        RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType);

        // Adjust command for specific error scenarios based on the "Given" descriptions
        if (aggregate.id().equals("screen-error-001")) {
            // Simulate missing mandatory fields in the command for the error scenario
             cmd = new RenderScreenCmd(null, null);
        } else if (aggregate.id().equals("screen-bms-001")) {
            // Simulate invalid length for BMS constraint
            cmd = new RenderScreenCmd(screenId, "DEVICE_TYPE_TOO_LONG_FOR_BMS");
        }

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("screen.rendered", resultingEvents.get(0).type());
        Assertions.assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        Assertions.assertEquals("screen-001", event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(
            thrownException instanceof IllegalArgumentException || 
            thrownException instanceof IllegalStateException
        );
    }
}
