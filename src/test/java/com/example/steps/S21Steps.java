package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class S21Steps {

    // Ideally injected via Cucumber-Spring, but using explicit instantiation for simpler JUnit runner
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    
    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String currentScreenId;
    private String currentDeviceType;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new ScreenMapAggregate(id);
        // We don't save to repo in this step unless we need to reload, 
        // but the repository holds the state if we did.
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        // The aggregate itself is valid, but we will provide invalid command data
        String id = UUID.randomUUID().toString();
        this.aggregate = new ScreenMapAggregate(id);
        // Setup state for the next steps
        this.currentScreenId = null; // Violates mandatory
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new ScreenMapAggregate(id);
        // Setup state: BMS fields usually max 12 chars for map names, screen names, etc.
        this.currentScreenId = "THIS-SCREEN-ID-IS-WAY-TOO-LONG-FOR-BMS"; 
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        this.currentScreenId = "SCRN01";
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        this.currentDeviceType = "DESKTOP";
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), currentScreenId, currentDeviceType);
            this.resultEvents = aggregate.execute(cmd);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for the specific validation exception types (IllegalArgumentException or IllegalStateException)
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected a domain exception (IllegalArgumentException or IllegalStateException), but got: " + capturedException.getClass().getSimpleName()
        );
    }

    // In-memory implementation for test purposes
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();

        @Override
        public ScreenMapAggregate load(String id) {
            return store.get(id);
        }

        @Override
        public void save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
    }
}
