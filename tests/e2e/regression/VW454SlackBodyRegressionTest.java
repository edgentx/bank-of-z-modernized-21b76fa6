package e2e.regression;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.domain.reporting.model.DefectReportingAggregate;
import com.example.domain.reporting.model.ReportDefectCmd;
import com.example.mocks.InMemoryGitHubIssuePort;
import com.example.mocks.InMemorySlackNotificationPort;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * VW-454 regression — end-to-end proof that when the platform's _report_defect
 * path emits a DefectReported event, the Slack body it carries contains the
 * GitHub issue URL.
 *
 * The defect (reported via VForce360 PM diagnostics) was that the Slack message
 * posted to #vforce360-issues was missing the "GitHub issue: <url>" line.
 *
 * This suite locks in three cases:
 *   1. Happy path — URL present and embedded in body.
 *   2. End-to-end — port-adapter wiring: GH port returns URL, command uses it,
 *      Slack port receives the body, body contains the same URL.
 *   3. Visible-missing — a blank URL still renders the "GitHub issue:" prefix
 *      so on-call sees the gap rather than silently dropping the line.
 */
public class VW454SlackBodyRegressionTest {

  @Test
  void slackBody_includes_githubIssueUrl_on_happy_path() {
    var agg = new DefectReportingAggregate("DEF-1");
    var cmd = new ReportDefectCmd(
        "DEF-1",
        "Validating VW-454 — GitHub URL in Slack body (end-to-end)",
        "LOW",
        "validation",
        "https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6/issues/42");
    var events = agg.execute(cmd);
    assertEquals(1, events.size());
    var ev = (DefectReportedEvent) events.get(0);
    assertEquals("defect.reported", ev.type());
    assertTrue(
        ev.slackBody().contains("GitHub issue: " + cmd.githubIssueUrl()),
        "Slack body must contain the GitHub issue URL line. Got: " + ev.slackBody());
  }

  @Test
  void endToEnd_githubPort_to_slackPort_preserves_url() {
    GitHubIssuePort gh = new InMemoryGitHubIssuePort();
    SlackNotificationPort slack = new InMemorySlackNotificationPort();

    String url = gh.openIssue("VW-454 e2e", "see ACs", "LOW");

    var agg = new DefectReportingAggregate("DEF-2");
    var cmd = new ReportDefectCmd("DEF-2", "VW-454 e2e", "LOW", "validation", url);
    var ev = (DefectReportedEvent) agg.execute(cmd).get(0);

    // Adapter step — Slack port receives the body the aggregate produced.
    slack.postToChannel("#vforce360-issues", ev.slackBody());

    var posted = ((InMemorySlackNotificationPort) slack).last();
    assertNotNull(posted, "expected one Slack post");
    assertEquals("#vforce360-issues", posted.channel());
    assertTrue(
        posted.body().contains(url),
        "Slack body delivered to channel must contain the GitHub URL. Got: " + posted.body());
  }

  @Test
  void slackBody_keeps_prefix_when_url_missing() {
    var agg = new DefectReportingAggregate("DEF-3");
    var cmd = new ReportDefectCmd("DEF-3", "missing-url case", "LOW", "validation", "");
    var ev = (DefectReportedEvent) agg.execute(cmd).get(0);
    assertTrue(
        ev.slackBody().contains("GitHub issue:"),
        "Body must still render the GitHub issue prefix so on-call sees the gap. Got: "
            + ev.slackBody());
  }
}
