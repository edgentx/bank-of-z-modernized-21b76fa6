package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S26Steps {

    private DataSyncCheckpoint aggregate;
    private VerifyDataParityCmd currentCmd;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid DataSyncCheckpoint aggregate")
    public void aValidDataSyncCheckpointAggregate() {
        String id = "checkpoint-1";
        aggregate = new DataSyncCheckpoint(id);
        // Initialize the aggregate state by applying a past checkpoint event
        // This simulates a loaded aggregate from the repo.
        var initCmd = new RecordSyncCheckpointCmd(id, 100L, "hash-123");
        aggregate.execute(initCmd);
        aggregate.clearEvents(); // Clear commit log from initialization
    }

    @And("a valid entityType is provided")
    public void aValidEntityTypeIsProvided() {
        // Command construction is deferred to the 'When' step
    }

    @And("a valid dateRange is provided")
    public void aValidDateRangeIsProvided() {
        // Command construction is deferred to the 'When' step
    }

    @When("the VerifyDataParityCmd command is executed")
    public void theVerifyDataParityCmdCommandIsExecuted() {
        // Construct a valid command for the "Happy Path" scenario
        // Using an offset > 100 (from init) to satisfy invariants.
        currentCmd = new VerifyDataParityCmd("checkpoint-1", "Account", 150L, "2023-01-01/2023-01-31", "hash-abc");
        try {
            resultEvents = aggregate.execute(currentCmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a parity.verified event is emitted")
    public void aParityVerifiedEventIsEmitted() {
        assertNull(thrownException, "Should not throw exception: " + thrownException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ParityVerifiedEvent);
        
        ParityVerifiedEvent event = (ParityVerifiedEvent) resultEvents.get(0);
        assertEquals("checkpoint-1", event.aggregateId());
        assertEquals("Account", event.entityType());
    }

    // Scenario 2: Offsets must strictly increase
    @Given("a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.")
    public void aDataSyncCheckpointAggregateThatViolatesCheckpointOffsets() {
        // Initialize aggregate to offset 100
        String id = "checkpoint-2";
        aggregate = new DataSyncCheckpoint(id);
        var initCmd = new RecordSyncCheckpointCmd(id, 100L, "hash-init");
        aggregate.execute(initCmd);
        aggregate.clearEvents();

        // Prepare a command with offset 50 (violates invariant)
        currentCmd = new VerifyDataParityCmd(id, "Account", 50L, "range", "hash-new");
    }

    // When step is reused from above

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // The existing implementation throws IllegalStateException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("strictly increase") || thrownException.getMessage().contains("validation"));
    }

    // Scenario 3: Data validation must pass
    @Given("a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.")
    public void aDataSyncCheckpointAggregateThatViolatesDataValidation() {
        // Initialize aggregate
        String id = "checkpoint-3";
        aggregate = new DataSyncCheckpoint(id);
        var initCmd = new RecordSyncCheckpointCmd(id, 10L, "hash-init");
        aggregate.execute(initCmd);
        aggregate.clearEvents();

        // Prepare a command with valid offset but BLANK validation hash
        // This triggers the validation invariant
        currentCmd = new VerifyDataParityCmd(id, "Account", 20L, "range", "   ");
    }

    // When and Then steps are reused from above
}
