package com.example.steps;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.shared.DomainEvent;

import java.util.List;

/**
 * Scenario-scoped shared state for DataSyncCheckpoint-aggregate Cucumber
 * steps (S-25 RecordSyncCheckpoint, S-26 VerifyDataParity).
 *
 * S-25 needs a NEW checkpoint aggregate; S-26 needs one already initialized
 * with a baseline RecordSyncCheckpoint event (so VerifyDataParity has
 * something to verify against). The {@code initialized} flag carries that
 * setup decision out of the shared @Given and into each story's @When so
 * the two stories can share the same "a valid DataSyncCheckpoint aggregate"
 * Gherkin step without colliding.
 */
public class DataSyncCheckpointSharedContext {
    public DataSyncCheckpoint aggregate;
    public boolean initialized;
    public List<DomainEvent> resultingEvents;
}
