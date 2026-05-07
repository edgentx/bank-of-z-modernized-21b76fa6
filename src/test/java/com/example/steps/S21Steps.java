package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import com.example.domain.uimodel.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    // Test Context
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SM-001");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Defer command creation until all parts are known
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Construct command here or in When step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command if not specified otherwise by context
        if (cmd == null) {
            cmd = new RenderScreenCmd("SM-001", "ACCTSUM", "3270");
        }
        try {
            resultEvents = aggregate.execute(cmd);
            // Commit state to simulate repository persistence logic
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("SM-001", event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
    }

    // Negative Scenarios
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("SM-INVALID-001");
        repository.save(aggregate);
        // Setup command with null screenId to violate constraint
        cmd = new RenderScreenCmd("SM-INVALID-001", null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SM-INVALID-002");
        repository.save(aggregate);
        // Setup command with screenId > 8 chars
        cmd = new RenderScreenCmd("SM-INVALID-002", "LONGSCREENNAME", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected exception but command succeeded");
        // Verify it's an IllegalArgumentException or IllegalStateException (domain error)
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException/IllegalStateException), got: " + capturedException.getClass().getSimpleName()
        );
        // Verify NO events were emitted
        Assertions.assertTrue(aggregate.uncommittedEvents().isEmpty());
    }
}