package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-25 (RecordSyncCheckpointCmd).
 * Shared DataSyncCheckpoint Givens + the rejection @Then live in
 * {@link DataSyncCheckpointSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link DataSyncCheckpointSharedContext}.
 */
public class S25Steps {

    private final DataSyncCheckpointSharedContext ctx;
    private final ScenarioContext sc;

    public S25Steps(DataSyncCheckpointSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the RecordSyncCheckpointCmd command is executed")
    public void the_RecordSyncCheckpointCmd_command_is_executed() {
        DataSyncCheckpoint aggregate = ctx.aggregate;
        // S-25 violation paths leave the aggregate either initialized at offset 100
        // (strict-increase trap → retry at offset 50) or empty with a bad hash
        // (data-validation trap → submit blank hash). Detect from the seeded
        // aggregate id so this single @When can drive every S-25 scenario.
        long offset = 100L;
        String hash = "hash-abc";
        if ("checkpoint-strict-increase".equals(aggregate.id())) {
            offset = 50L;
            hash = "hash-retry";
        } else if ("checkpoint-bad-hash".equals(aggregate.id())) {
            offset = 10L;
            hash = "";
        }
        RecordSyncCheckpointCmd cmd = new RecordSyncCheckpointCmd(aggregate.id(), offset, hash);
        try {
            ctx.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            sc.thrownException = e;
        }
    }

    @Then("a checkpoint.recorded event is emitted")
    public void a_checkpoint_recorded_event_is_emitted() {
        Assertions.assertNull(sc.thrownException, "Should not have thrown: " + sc.thrownException);
        List<DomainEvent> events = ctx.resultingEvents;
        Assertions.assertNotNull(events, "Events should not be null");
        Assertions.assertFalse(events.isEmpty());
        Assertions.assertEquals("checkpoint.recorded", events.get(0).type());
    }
}
