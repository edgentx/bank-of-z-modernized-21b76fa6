package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // In-memory implementation for testing
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();
        @Override
        public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }
        @Override
        public ScreenMapAggregate findById(String id) {
            return store.get(id);
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("SCREEN_LOGIN");
        this.aggregate.initializeField("USER_ID", 10, true, 1); // len, mandatory, min
        this.aggregate.initializeField("PASSWORD", 20, true, 1);
        repository.save(this.aggregate);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled by the aggregate state, or setup here if needed
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Implicitly handled in the 'When' step construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("SCREEN_MANDATORY");
        this.aggregate.initializeField("REQUIRED_FIELD", 10, true, 1); // mandatory
        this.aggregate.initializeField("OPTIONAL_FIELD", 10, false, 0);
        repository.save(this.aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        this.aggregate = new ScreenMapAggregate("SCREEN_BMS");
        this.aggregate.initializeField("SHORT_FIELD", 5, true, 1); // Max length 5
        repository.save(this.aggregate);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs;
        String screenId = aggregate.id();

        if (screenId.equals("SCREEN_LOGIN")) {
            inputs = Map.of("USER_ID", "alice", "PASSWORD", "secret");
        } else if (screenId.equals("SCREEN_MANDATORY")) {
            // Missing 'REQUIRED_FIELD'
            inputs = Map.of("OPTIONAL_FIELD", "data");
        } else if (screenId.equals("SCREEN_BMS")) {
            // Exceeds length 5
            inputs = Map.of("SHORT_FIELD", "waytoolong");
        } else {
            inputs = Map.of();
        }

        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenId, inputs);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        Assertions.assertEquals("input.validated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // We expect either IllegalArgumentException or IllegalStateException based on aggregate logic
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
