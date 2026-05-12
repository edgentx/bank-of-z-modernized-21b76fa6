package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by every DataSyncCheckpoint-aggregate story
 * (S-25 RecordSyncCheckpoint, S-26 VerifyDataParity).
 *
 * The "a valid DataSyncCheckpoint aggregate" Given seeds a fresh aggregate;
 * each story's @When initializes it with a baseline checkpoint if it needs
 * one (S-26 success path), or leaves it empty (S-25 success path). The
 * shared violation @Givens set up the strictly-increasing / data-validation
 * traps both stories exercise.
 */
public class DataSyncCheckpointSharedSteps {

    private final DataSyncCheckpointSharedContext ctx;

    public DataSyncCheckpointSharedSteps(DataSyncCheckpointSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid DataSyncCheckpoint aggregate")
    public void aValidDataSyncCheckpointAggregate() {
        ctx.aggregate = new DataSyncCheckpoint("checkpoint-1");
        ctx.initialized = false;
    }

    @And("a valid syncOffset is provided")
    public void aValidSyncOffsetIsProvided() {
        // Prepared in @When.
    }

    @And("a valid validationHash is provided")
    public void aValidValidationHashIsProvided() {
        // Prepared in @When.
    }

    @And("a valid entityType is provided")
    public void aValidEntityTypeIsProvided() {
        // Prepared in @When.
    }

    @And("a valid dateRange is provided")
    public void aValidDateRangeIsProvided() {
        // Prepared in @When.
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.")
    public void aDataSyncCheckpointAggregateThatViolatesOffsets() {
        ctx.aggregate = new DataSyncCheckpoint("checkpoint-strict-increase");
        ctx.aggregate.execute(new RecordSyncCheckpointCmd(ctx.aggregate.id(), 100L, "hash-init"));
        ctx.aggregate.clearEvents();
        ctx.initialized = true;
    }

    @Given("a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.")
    public void aDataSyncCheckpointAggregateThatViolatesDataValidation() {
        ctx.aggregate = new DataSyncCheckpoint("checkpoint-bad-hash");
        ctx.initialized = false;
    }
}
