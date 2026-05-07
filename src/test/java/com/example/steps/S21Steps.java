package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private ScreenMapAggregate store;
        @Override public ScreenMapAggregate save(ScreenMapAggregate aggregate) { return this.store = aggregate; }
        @Override public Optional<ScreenMapAggregate> findById(String id) { return Optional.ofNullable(store); }
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("sm-1");
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Valid ID for BMS constraint (<= 8 chars)
        // We construct the command in the 'When' step, or store parts here.
        // For simplicity, we'll assume the cmd is built valid by default in 'When'
        // or we store state. Let's store valid parts.
        // However, the scenario implies specific setup.
        // Let's keep it simple: set up a default valid command builder pattern or just handle it in When.
        // For this specific step, it's a no-op if we handle defaults in When.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Same as above
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        this.aggregate = new ScreenMapAggregate("sm-2");
        this.cmd = new RenderScreenCmd("", "3270"); // Blank screenId
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("sm-3");
        this.cmd = new RenderScreenCmd("VERY_LONG_SCREEN_ID", "3270"); // > 8 chars
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // If cmd is not set by specific violation steps, use valid defaults
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("ACCTMENU", "3270");
        }
        try {
            // Reload to simulate clean fetch or use current instance
            this.resultEvents = this.aggregate.execute(this.cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("ACCTMENU", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
