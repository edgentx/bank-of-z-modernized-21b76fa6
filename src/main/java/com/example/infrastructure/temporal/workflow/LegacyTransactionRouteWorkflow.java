package com.example.infrastructure.temporal.workflow;

import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingInput;
import com.example.infrastructure.temporal.saga.LegacyTransactionRoutingResult;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * BANK S-33 — Temporal workflow contract for CICS/IMS legacy transaction
 * routing.
 *
 * <p>Each invocation:
 * <ol>
 *   <li>evaluates the routing rule for the requested transaction,</li>
 *   <li>dispatches to either the modernized handler or the legacy CICS/IMS
 *       bridge — the latter under Temporal's retry policy so transient
 *       mainframe link blips don't surface to callers,</li>
 *   <li>records a sync checkpoint via the legacy-bridge service so the
 *       data-parity verifier can confirm post-cutover that both ledgers
 *       agree.</li>
 * </ol>
 */
@WorkflowInterface
public interface LegacyTransactionRouteWorkflow {

  @WorkflowMethod
  LegacyTransactionRoutingResult route(LegacyTransactionRoutingInput input);

  @QueryMethod
  String currentStep();
}
