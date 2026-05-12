package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.ParityVerifiedEvent;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.legacybridge.model.VerifyDataParityCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-26 (VerifyDataParityCmd).
 * Shared DataSyncCheckpoint Givens + the rejection @Then live in
 * {@link DataSyncCheckpointSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link DataSyncCheckpointSharedContext}.
 */
public class S26Steps {

    private final DataSyncCheckpointSharedContext ctx;
    private final ScenarioContext sc;

    public S26Steps(DataSyncCheckpointSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the VerifyDataParityCmd command is executed")
    public void the_VerifyDataParityCmd_command_is_executed() {
        DataSyncCheckpoint aggregate = ctx.aggregate;
        // S-26 success expects an aggregate already at offset 100. The shared
        // "a valid DataSyncCheckpoint aggregate" Given seeds an empty aggregate
        // (S-25's success path needs that), so VerifyDataParity bootstraps the
        // baseline checkpoint here when it has not already been seeded by a
        // violation Given.
        if (!ctx.initialized) {
            aggregate.execute(new RecordSyncCheckpointCmd(aggregate.id(), 100L, "hash-baseline"));
            aggregate.clearEvents();
            ctx.initialized = true;
        }

        long offset = 150L;
        String hash = "hash-abc";
        if ("checkpoint-strict-increase".equals(aggregate.id())) {
            offset = 50L; // violates strict-increase: baseline is 100
        } else if ("checkpoint-bad-hash".equals(aggregate.id())) {
            offset = 20L;
            hash = "   ";
        }
        VerifyDataParityCmd cmd = new VerifyDataParityCmd(
                aggregate.id(), "Account", offset, "2026-01-01/2026-01-31", hash);
        try {
            ctx.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            sc.thrownException = e;
        }
    }

    @Then("a parity.verified event is emitted")
    public void a_parity_verified_event_is_emitted() {
        assertNull(sc.thrownException, "Should not throw exception: " + sc.thrownException);
        List<DomainEvent> events = ctx.resultingEvents;
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ParityVerifiedEvent);

        ParityVerifiedEvent event = (ParityVerifiedEvent) events.get(0);
        assertEquals(ctx.aggregate.id(), event.aggregateId());
        assertEquals("Account", event.entityType());
    }
}
