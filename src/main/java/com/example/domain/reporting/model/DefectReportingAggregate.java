package com.example.domain.reporting.model;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class DefectReportingAggregate extends AggregateRoot {
  private final String defectId;
  private boolean reported;

  public DefectReportingAggregate(String defectId) { this.defectId = defectId; }
  @Override public String id() { return defectId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof ReportDefectCmd c) {
      if (reported) throw new IllegalStateException("Defect already reported: " + c.defectId());
      if (c.title() == null || c.title().isBlank()) throw new IllegalArgumentException("title required");
      if (c.severity() == null || c.severity().isBlank()) throw new IllegalArgumentException("severity required");

      // VW-454 invariant: the Slack body must include the GitHub issue line end-to-end.
      // Kept even when the URL is empty so a missing URL is visible to the on-call channel,
      // rather than silently dropped.
      String url = c.githubIssueUrl() == null ? "" : c.githubIssueUrl();
      String slackBody = buildSlackBody(c.title(), c.severity(), c.component(), url);

      var event = new DefectReportedEvent(
          c.defectId(), c.title(), c.severity(), c.component(), url, slackBody, Instant.now());
      this.reported = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isReported() { return reported; }

  static String buildSlackBody(String title, String severity, String component, String githubIssueUrl) {
    StringBuilder sb = new StringBuilder();
    sb.append("*Defect:* ").append(title).append('\n');
    sb.append("*Severity:* ").append(severity).append('\n');
    if (component != null && !component.isBlank()) {
      sb.append("*Component:* ").append(component).append('\n');
    }
    sb.append("GitHub issue: ").append(githubIssueUrl);
    return sb.toString();
  }
}
