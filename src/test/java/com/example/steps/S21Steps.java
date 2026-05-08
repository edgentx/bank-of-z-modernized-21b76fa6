package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Repository Implementation for Testing
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

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled in the 'When' step via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Implicitly handled in the 'When' step via command construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_violating_mandatory_inputs() {
        aggregate = new ScreenMapAggregate("screen-violation-1");
        // We will construct a bad command in the When step
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-violation-2");
        // We will construct a bad command in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Determine context based on scenario state (heuristic)
            RenderScreenCmd cmd;
            if (aggregate.id().equals("screen-violation-1")) {
                // Missing mandatory fields (screenId)
                cmd = new RenderScreenCmd("", "3270", new HashMap<>());
            } else if (aggregate.id().equals("screen-violation-2")) {
                // Field length violation
                Map<String, String> badLayout = new HashMap<>();
                badLayout.put("field1", "A".repeat(300)); // Exceeds limit
                cmd = new RenderScreenCmd("screen-2", "3270", badLayout);
            } else {
                // Valid command
                Map<String, String> layout = new HashMap<>();
                layout.put("field1", "value1");
                cmd = new RenderScreenCmd("screen-123", "web", layout);
            }
            
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException
        );
    }
}
