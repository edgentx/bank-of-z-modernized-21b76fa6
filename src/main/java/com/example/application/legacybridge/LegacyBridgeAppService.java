package com.example.application.legacybridge;

import com.example.application.AggregateNotFoundException;
import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.VerifyDataParityCmd;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Legacy-bridge application service.
 *
 * Holds in-memory state for {@link LegacyTransactionRoute} and
 * {@link DataSyncCheckpoint} aggregates because the legacybridge bounded
 * context does not yet ship a persistence adapter — by design, since the
 * canonical {@code legacytransactionroute.*} scaffold aggregate (which DOES
 * have a Mongo adapter) is a deliberate stub and the rich legacybridge
 * variants own all the routing/parity logic for S-30. A future story can
 * promote these to Mongo-backed adapters via a dedicated port.
 */
@Service
public class LegacyBridgeAppService {

  private final Map<String, LegacyTransactionRoute> routes = new ConcurrentHashMap<>();
  private final Map<String, DataSyncCheckpoint> checkpoints = new ConcurrentHashMap<>();

  public LegacyTransactionRoute evaluateRouting(EvaluateRoutingCmd cmd) {
    LegacyTransactionRoute route = routes.computeIfAbsent(
        cmd.routeId(), LegacyTransactionRoute::new);
    route.execute(cmd);
    return route;
  }

  public LegacyTransactionRoute updateRoutingRule(String routeId, UpdateRoutingRuleCmd cmd) {
    LegacyTransactionRoute route = routes.get(routeId);
    if (route == null) {
      throw new AggregateNotFoundException("LegacyTransactionRoute", routeId);
    }
    route.execute(cmd);
    return route;
  }

  public LegacyTransactionRoute findRoute(String routeId) {
    LegacyTransactionRoute route = routes.get(routeId);
    if (route == null) {
      throw new AggregateNotFoundException("LegacyTransactionRoute", routeId);
    }
    return route;
  }

  public DataSyncCheckpoint recordCheckpoint(RecordSyncCheckpointCmd cmd) {
    DataSyncCheckpoint checkpoint = checkpoints.computeIfAbsent(
        cmd.checkpointId(), DataSyncCheckpoint::new);
    checkpoint.execute(cmd);
    return checkpoint;
  }

  public DataSyncCheckpoint verifyDataParity(VerifyDataParityCmd cmd) {
    DataSyncCheckpoint checkpoint = checkpoints.computeIfAbsent(
        cmd.checkpointId(), DataSyncCheckpoint::new);
    checkpoint.execute(cmd);
    return checkpoint;
  }

  public DataSyncCheckpoint findCheckpoint(String checkpointId) {
    DataSyncCheckpoint checkpoint = checkpoints.get(checkpointId);
    if (checkpoint == null) {
      throw new AggregateNotFoundException("DataSyncCheckpoint", checkpointId);
    }
    return checkpoint;
  }
}
