package com.example.domain.reporting.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate for the platform's PM diagnostic _report_defect path.
 * Fixes VW-454 by guaranteeing the GitHub issue URL is embedded in the
 * Slack notification body emitted to #vforce360-issues.
 */
public class DefectReportingAggregate extends AggregateRoot {

  private final String id;
  private final GitHubIssuePort gitHub;
  private final SlackNotificationPort slack;

  public DefectReportingAggregate(String id, GitHubIssuePort gitHub, SlackNotificationPort slack) {
    this.id = id;
    this.gitHub = gitHub;
    this.slack = slack;
  }

  @Override public String id() { return id; }

  @Override
  public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof ReportDefectCmd rd) {
      return List.of(handle(rd));
    }
    throw new UnknownCommandException("DefectReportingAggregate: " + cmd.getClass().getSimpleName());
  }

  private DefectReportedEvent handle(ReportDefectCmd cmd) {
    String issueUrl = gitHub.createIssue(cmd.title(), cmd.description());
    String body = buildSlackBody(cmd, issueUrl);
    slack.sendNotification(body);

    DefectReportedEvent event = new DefectReportedEvent(
        id, cmd.defectId(), issueUrl, body, Instant.now());
    addEvent(event);
    incrementVersion();
    return event;
  }

  private static String buildSlackBody(ReportDefectCmd cmd, String issueUrl) {
    // VW-454 contract: the GitHub URL MUST appear in the Slack body.
    // We include it even when empty so downstream readers can see the gap
    // instead of silently dropping the line.
    return String.format(
        "Defect %s reported: %s%nGitHub issue: %s",
        cmd.defectId(), cmd.title(), issueUrl == null ? "" : issueUrl);
  }
}
