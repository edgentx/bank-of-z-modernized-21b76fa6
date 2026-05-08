package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Scenario 1: Success ---
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        String id = "screen-map-1";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicitly handled in the command construction, but we assume state is ready
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Implicitly handled in command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        RenderScreenCmd cmd = new RenderScreenCmd(
                "screen-map-1",
                "LOGIN_SCREEN",
                "WEB_DESKTOP",
                Map.of("username", "testUser")
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    // --- Scenario 2: Mandatory Fields ---
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("invalid-screen");
    }

    @When("the RenderScreenCmd command is executed")
    public void executeRenderCmdInvalidMandatory() {
        // Missing screenId
        RenderScreenCmd cmd = new RenderScreenCmd("invalid-screen", null, "WEB", Map.of());
        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("mandatory"));
    }

    // --- Scenario 3: BMS Constraints ---
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("bms-violator");
    }

    @When("the RenderScreenCmd command is executed")
    public void executeRenderCmdInvalidBMS() {
        // Create a field value > 80 chars
        String longString = "a".repeat(100);
        RenderScreenCmd cmd = new RenderScreenCmd("bms-violator", "INPUT_SCREEN", "3270", Map.of("longField", longString));
        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // In-memory implementation for testing
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new java.util.HashMap<>();

        @Override
        public java.util.Optional<ScreenMapAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store.get(id));
        }

        @Override
        public void save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
    }
}
