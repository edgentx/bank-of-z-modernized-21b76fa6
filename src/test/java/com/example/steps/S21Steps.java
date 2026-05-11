package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    // Sufficiently unique ID for this scenario
    private final String screenMapId = "SM-TEST-" + System.currentTimeMillis();
    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repo = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // In-memory implementation for test isolation
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private ScreenMapAggregate store;
        @Override
        public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            this.store = aggregate;
            return aggregate;
        }
        @Override
        public Optional<ScreenMapAggregate> findById(String id) {
            return Optional.ofNullable(store);
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate(screenMapId);
        repo.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // State handled in the 'When' block via Command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // State handled in the 'When' block via Command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, "LOGIN_SCR", "DESKTOP_WEB");
            // Reload from repo to ensure clean state if needed, or use instance
            ScreenMapAggregate agg = repo.findById(screenMapId).orElseThrow();
            resultEvents = agg.execute(cmd);
            repo.save(agg); // Persist state change
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    // --- Negative Cases ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        try {
            // Passing null for screenId to violate the invariant
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, null, "MOBILE");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory") || 
                   capturedException.getMessage().contains("BMS") ||
                   capturedException.getMessage().contains("required"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @When("the RenderScreenCmd command is executed with BMS violating data")
    public void theRenderScreenCmdCommandIsExecutedWithBmsViolatingData() {
        try {
            // Create a screenId > 32 chars
            String longScreenId = "VERY_LONG_SCREEN_ID_THAT_EXCEEDS_THIRTY_TWO_CHARS";
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, longScreenId, "DESKTOP");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
