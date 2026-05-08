package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    // In-Memory Repository implementation for testing
    public static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();
        @Override
        public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }
        @Override
        public Optional<ScreenMapAggregate> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }
    }

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
        aggregate.setRequiresMandatoryFields(true);
        aggregate.setMaxFieldLength(10);
        // Save to repo to simulate persistence
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFieldValidation() {
        aggregate = new ScreenMapAggregate("screen-456");
        aggregate.setRequiresMandatoryFields(true);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthConstraints() {
        aggregate = new ScreenMapAggregate("screen-789");
        aggregate.setRequiresMandatoryFields(false); // Ensure mandatory check passes
        aggregate.setMaxFieldLength(5); // Set a strict limit
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "screen-123";
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        this.inputFields = new HashMap<>();
        this.inputFields.put("mandatoryField1", "validValue");
        this.inputFields.put("optionalField", "opt");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Reload aggregate to ensure clean state if needed, though direct usage is fine here
            var cmd = new ValidateScreenInputCmd(screenId != null ? screenId : aggregate.id(), inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("screen.input.validated", resultEvents.get(0).type());
        assertNull(thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }
}
