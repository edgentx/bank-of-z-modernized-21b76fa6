package e2e.regression;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.domain.reporting.model.DefectReportingAggregate;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.InMemorySlackNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for VW-454 — Story S-FB-1.
 *
 * Defect: the Slack body posted to #vforce360-issues by the platform's
 * _report_defect path was missing the GitHub issue URL.
 * Contract: every defect report ends up in Slack with the issue link line.
 */
public class VW454SlackBodyRegressionTest {

  private InMemoryGitHubIssuePort gitHub;
  private InMemorySlackNotificationPort slack;
  private DefectReportingAggregate aggregate;

  @BeforeEach
  void setUp() {
    gitHub = new InMemoryGitHubIssuePort();
    slack = new InMemorySlackNotificationPort();
    aggregate = new DefectReportingAggregate("agg-vw-454", gitHub, slack);
  }

  @Test
  void slackBodyContainsGitHubIssueUrl() {
    String issueUrl = "https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6/issues/454";
    gitHub.setNextIssueUrl(issueUrl);

    ReportDefectCmd cmd = new ReportDefectCmd(
        "VW-454",
        "Validating VW-454 — GitHub URL in Slack body",
        "Severity: LOW / Component: validation");

    List<DomainEvent> events = aggregate.execute(cmd);

    assertThat(events).hasSize(1);
    assertThat(events.get(0)).isInstanceOf(DefectReportedEvent.class);

    String body = slack.getLastMessage();
    assertThat(body)
        .as("Slack body must include the GitHub issue URL line (VW-454 contract)")
        .isNotNull()
        .contains(issueUrl)
        .contains("GitHub issue:");
  }

  @Test
  void eventPayloadCarriesGitHubUrl() {
    String issueUrl = "https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6/issues/999";
    gitHub.setNextIssueUrl(issueUrl);

    var events = aggregate.execute(new ReportDefectCmd("D-1", "title", "desc"));

    DefectReportedEvent event = (DefectReportedEvent) events.get(0);
    assertThat(event.githubUrl()).isEqualTo(issueUrl);
    assertThat(event.slackMessageBody()).contains(issueUrl);
  }

  @Test
  void emptyGitHubUrlStillEmitsSlackMessageWithLabel() {
    gitHub.setNextIssueUrl("");

    aggregate.execute(new ReportDefectCmd("D-2", "title", "desc"));

    String body = slack.getLastMessage();
    assertThat(body)
        .as("Even with no URL, the label line must be present so readers see the gap")
        .isNotNull()
        .contains("GitHub issue:");
  }
}
