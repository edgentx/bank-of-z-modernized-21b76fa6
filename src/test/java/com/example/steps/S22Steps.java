package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // In-memory repository stub to satisfy interface
    private final ScreenMapRepository repo = new ScreenMapRepository() {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();
        @Override public void save(ScreenMapAggregate a) { store.put(a.id(), a); }
        @Override public Optional<ScreenMapAggregate> findById(String id) { return Optional.ofNullable(store.get(id)); }
    };

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in construction or setup, typically we assume the aggregate ID matches the command target
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> fields = new HashMap<>();
        fields.put("user", "john_doe");
        fields.put("pass", "secret123");
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // Persist state change
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertInstanceOf(ScreenInputValidatedEvent.class, resultEvents.get(0));
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        // Use a specific screenId that triggers the mandatory check logic in the aggregate
        aggregate = new ScreenMapAggregate("MANDATORY");
        Map<String, String> incompleteFields = new HashMap<>();
        // Missing 'requiredField' which the aggregate expects for 'MANDATORY' screen
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), incompleteFields);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("LONG_FIELD_SCREEN");
        Map<String, String> longFields = new HashMap<>();
        longFields.put("desc", "This description is definitely way too long for legacy BMS buffers.");
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), longFields);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Verify it's a specific error type if needed, though RuntimeException is sufficient for BDD step verification
    }
}
