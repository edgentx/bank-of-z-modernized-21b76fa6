package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S25Steps {

    private DataSyncCheckpoint aggregate;
    private Exception capturedException;
    private String lastEvent;

    @Given("a valid DataSyncCheckpoint aggregate")
    public void a_valid_DataSyncCheckpoint_aggregate() {
        aggregate = new DataSyncCheckpoint("checkpoint-1");
    }

    @Given("a valid syncOffset is provided")
    public void a_valid_syncOffset_is_provided() {
        // Context setup, usually handled in the When block via Command construction
    }

    @Given("a valid validationHash is provided")
    public void a_valid_validationHash_is_provided() {
        // Context setup
    }

    @When("the RecordSyncCheckpointCmd command is executed")
    public void the_RecordSyncCheckpointCmd_command_is_executed() {
        // Execute valid command based on context of previous Givens
        RecordSyncCheckpointCmd cmd = new RecordSyncCheckpointCmd("checkpoint-1", 100L, "hash-abc");
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = events.get(0).type();
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.")
    public void a_DataSyncCheckpoint_aggregate_that_violates_Checkpoint_offsets_must_strictly_increase() {
        aggregate = new DataSyncCheckpoint("checkpoint-2");
        // Set initial offset to 100
        aggregate.execute(new RecordSyncCheckpointCmd("checkpoint-2", 100L, "initial"));
        capturedException = null;
    }

    // Specific When for the violation context to ensure correct command parameters
    @When("the RecordSyncCheckpointCmd command is executed with offset 50")
    public void the_RecordSyncCheckpointCmd_command_is_executed_with_lower_offset() {
        RecordSyncCheckpointCmd cmd = new RecordSyncCheckpointCmd("checkpoint-2", 50L, "hash-xyz");
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.")
    public void a_DataSyncCheckpoint_aggregate_that_violates_data_validation() {
        aggregate = new DataSyncCheckpoint("checkpoint-3");
        capturedException = null;
    }

    @When("the RecordSyncCheckpointCmd command is executed with invalid hash")
    public void the_RecordSyncCheckpointCmd_command_is_executed_with_invalid_hash() {
        // Empty hash violates the validation check
        RecordSyncCheckpointCmd cmd = new RecordSyncCheckpointCmd("checkpoint-3", 10L, "");
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a checkpoint.recorded event is emitted")
    public void a_checkpoint_recorded_event_is_emitted() {
        Assertions.assertNotNull(lastEvent);
        Assertions.assertEquals("checkpoint.recorded", lastEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
