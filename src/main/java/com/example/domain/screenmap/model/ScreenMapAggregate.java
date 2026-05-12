package com.example.domain.screenmap.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;

/**
 * ScreenMapAggregate — user-interface-navigation bounded context.
 * Owns the layout/render contract for legacy BMS screens during the
 * mainframe→modern transition.
 *
 * Invariants enforced on RenderScreenCmd (BANK S-21):
 *   1. All mandatory input fields must be validated before screen submission.
 *   2. Field lengths must strictly adhere to legacy BMS constraints during
 *      the transition period.
 *
 * Aggregates start in the valid state (both invariants satisfied). Gherkin
 * "violates: X" Givens flip the corresponding state flag so the next command
 * exercises the rejection path, matching the pattern established by
 * TellerSessionAggregate.
 */
public class ScreenMapAggregate extends AggregateRoot {
  private final String id;
  private boolean mandatoryFieldsValidated = true;
  private boolean bmsFieldLengthCompliant = true;

  public ScreenMapAggregate(String id) { this.id = id; }

  @Override public String id() { return id; }

  public void setMandatoryFieldsValidated(boolean mandatoryFieldsValidated) {
    this.mandatoryFieldsValidated = mandatoryFieldsValidated;
  }
  public void setBmsFieldLengthCompliant(boolean bmsFieldLengthCompliant) {
    this.bmsFieldLengthCompliant = bmsFieldLengthCompliant;
  }

  public boolean isMandatoryFieldsValidated() { return mandatoryFieldsValidated; }
  public boolean isBmsFieldLengthCompliant() { return bmsFieldLengthCompliant; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof RenderScreenCmd c) return renderScreen(c);
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> renderScreen(RenderScreenCmd c) {
    if (!mandatoryFieldsValidated) {
      throw new IllegalStateException(
        "Cannot render screen: mandatory input fields have not been validated before submission");
    }
    if (!bmsFieldLengthCompliant) {
      throw new IllegalStateException(
        "Cannot render screen: field lengths violate legacy BMS constraints");
    }
    var event = ScreenRenderedEvent.create(id, c.screenId(), c.deviceType());
    addEvent(event);
    incrementVersion();
    return List.of(event);
  }
}
