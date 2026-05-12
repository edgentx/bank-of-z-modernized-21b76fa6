package com.example.ports;

import java.util.Optional;

/**
 * BANK S-33 — Hex port for durable workflow orchestration.
 *
 * <p>Backed by Temporal in the modernized stack
 * ({@code com.example.infrastructure.temporal}), but the contract is provider-
 * neutral so the same port can sit in front of AWS Step Functions, Cadence,
 * or an in-memory test double when a deployment requires it.
 *
 * <p>The port intentionally exposes only the operational concerns that
 * application code needs to drive a workflow from the outside —
 * <em>start</em>, <em>query status</em>, <em>cancel</em> — and deliberately
 * does NOT leak Temporal-specific types ({@code WorkflowOptions},
 * {@code WorkflowExecution}) so {@code com.example.application.*} can stay
 * free of {@code io.temporal.*} imports.
 *
 * <p>The workflow + activity <em>implementations</em> themselves live in the
 * infrastructure layer because Temporal's programming model requires
 * deterministic workflow code annotated with framework annotations — those
 * are an adapter detail, not a domain concept.
 */
public interface WorkflowOrchestrationPort {

  /**
   * Start a new workflow execution.
   *
   * @param workflowType logical workflow name (e.g. {@code "AccountOpeningWorkflow"});
   *                     the adapter maps it to the Temporal workflow type identifier.
   * @param workflowId   business-meaningful identifier (e.g. {@code "open-acct-<uuid>"}).
   *                     Temporal uses this for deduplication: starting a
   *                     workflow with the same id while one is already running
   *                     is rejected per the configured ID-reuse policy.
   * @param input        opaque payload — the adapter serializes it via the
   *                     configured payload converter (JSON by default).
   * @return handle containing the workflow id, run id, and immediate status.
   * @throws WorkflowException when the underlying scheduler cannot accept the
   *         start (transport failure, namespace not registered, duplicate id
   *         under reject-duplicate policy, etc.).
   */
  WorkflowExecutionHandle startWorkflow(String workflowType, String workflowId, Object input);

  /**
   * Query the current status of a workflow execution. Returns
   * {@link Optional#empty()} when no execution with that id exists.
   *
   * <p>The {@code WorkflowStatus} value is derived from the Temporal
   * {@code WorkflowExecutionInfo.status} field so callers can act on
   * RUNNING/COMPLETED/FAILED/CANCELLED/TERMINATED/TIMED_OUT without binding
   * to the gRPC enum.
   *
   * @throws WorkflowException on transport or auth failures (lookup of a
   *         missing workflow id is NOT an error — it returns empty).
   */
  Optional<WorkflowStatus> queryStatus(String workflowId);

  /**
   * Request graceful cancellation of a running workflow. The workflow
   * receives a cancellation signal at the next yield and is expected to run
   * its compensation logic before exiting (Temporal's cancellation is
   * <em>cooperative</em>, not preemptive). No-op when the workflow is
   * already in a terminal state.
   *
   * @throws WorkflowException on transport failures.
   */
  void cancelWorkflow(String workflowId);
}
