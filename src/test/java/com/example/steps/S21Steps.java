package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class S21Steps {

    private ScreenMap aggregate;
    private final InMemoryScreenMapRepository repo = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMap> store = new HashMap<>();
        @Override public ScreenMap save(ScreenMap a) { store.put(a.id(), a); return a; }
        @Override public Optional<ScreenMap> findById(String id) { return Optional.ofNullable(store.get(id)); }
    }

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMap("SCREEN001");
        repo.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Logic handled in 'when' via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Logic handled in 'when' via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("SCREEN001", "3270");
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // persist state changes if any
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("SCREEN001", event.aggregateId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.layout());
    }

    // Negative Cases

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_all_mandatory_input_fields() {
        aggregate = new ScreenMap("SCREEN002");
        // The violation will be simulated by passing null/blank IDs in the When step for this context
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMap("SCREEN003");
        // The violation will be simulated by passing a very long ID in the When step
    }

    @When("the command is executed for invalid input")
    public void the_command_is_executed_for_invalid_input() {
        try {
            // Attempting to execute with bad data based on the 'Given' context above
            // We'll try to trigger the length constraint here
            String longId = "SCREEN-" + "X".repeat(100); 
            RenderScreenCmd cmd = new RenderScreenCmd(longId, "3270");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }
    
    @When("the command is executed for missing fields")
    public void the_command_is_executed_for_missing_fields() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("", "3270"); // Blank ID
            aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}