package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S25Steps {

    private DataSyncCheckpoint aggregate;
    private RecordSyncCheckpointCmd cmd;
    private List<DomainEvent> result;
    private Exception caughtException;

    @Given("a valid DataSyncCheckpoint aggregate")
    public void aValidDataSyncCheckpointAggregate() {
        aggregate = new DataSyncCheckpoint("ckpt-123");
    }

    @And("a valid syncOffset is provided")
    public void aValidSyncOffsetIsProvided() {
        // Default valid offset setup, assuming aggregate is new (offset 0)
        this.cmd = new RecordSyncCheckpointCmd("ckpt-123", 100, "hash-abc");
    }

    @And("a valid validationHash is provided")
    public void aValidValidationHashIsProvided() {
        // Handled in the previous step for default command construction, 
        // or we can update the command object here if it existed as a field.
    }

    @When("the RecordSyncCheckpointCmd command is executed")
    public void theRecordSyncCheckpointCmdCommandIsExecuted() {
        try {
            // If cmd wasn't set by specific violation step, ensure a default one exists
            if (cmd == null) cmd = new RecordSyncCheckpointCmd("ckpt-123", 100, "hash-abc");
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a checkpoint.recorded event is emitted")
    public void aCheckpointRecordedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("checkpoint.recorded", result.get(0).type());
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.")
    public void aDataSyncCheckpointAggregateThatViolatesCheckpointOffsets() {
        aggregate = new DataSyncCheckpoint("ckpt-violation-offset");
        // Simulate an existing checkpoint by executing one command manually (bypassing standard flow for test setup)
        // Or constructing the aggregate with a specific state.
        // Since there are no setters, we execute a command to set state.
        aggregate.execute(new RecordSyncCheckpointCmd("ckpt-violation-offset", 100, "hash-init"));
        aggregate.clearEvents(); // Clear setup events

        // Now setup the command with a lower offset to trigger violation
        this.cmd = new RecordSyncCheckpointCmd("ckpt-violation-offset", 99, "hash-bad");
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.")
    public void aDataSyncCheckpointAggregateThatViolatesDataValidation() {
        aggregate = new DataSyncCheckpoint("ckpt-violation-hash");
        // Setup command with invalid hash
        this.cmd = new RecordSyncCheckpointCmd("ckpt-violation-hash", 100, "");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception to be thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + caughtException.getClass()
        );
    }
}
