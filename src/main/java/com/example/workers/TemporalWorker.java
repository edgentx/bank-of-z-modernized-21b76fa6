package com.example.workers;

import com.example.application.ReportDefectWorkflowService;
import com.example.ports.GithubPort;
import com.example.ports.SlackPort;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class TemporalWorker {

    private final WorkerFactory factory;
    private final ReportDefectWorkflowService workflowService;
    private final GithubPort githubPort;
    private final SlackPort slackPort;

    @Autowired
    public TemporalWorker(WorkerFactory factory, ReportDefectWorkflowService workflowService, GithubPort githubPort, SlackPort slackPort) {
        this.factory = factory;
        this.workflowService = workflowService;
        this.githubPort = githubPort;
        this.slackPort = slackPort;
        register();
        factory.start();
    }

    private void register() {
        Worker worker = factory.newWorker("REPORT_DEFECT_TASK_QUEUE");
        // Register Activities
        worker.registerActivitiesImplementations(new ActivityImpl(githubPort, slackPort));
        // Register Workflow
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflowImpl.class,
            () -> new ReportDefectWorkflowImpl(workflowService));
    }

    @PreDestroy
    public void shutdown() {
        factory.shutdown();
    }

    public static class ActivityImpl implements ReportDefectActivity {
        private final GithubPort githubPort;
        private final SlackPort slackPort;

        public ActivityImpl(GithubPort githubPort, SlackPort slackPort) {
            this.githubPort = githubPort;
            this.slackPort = slackPort;
        }

        @Override
        public String createIssue(String title, String description) {
            return githubPort.createIssue(title, description);
        }

        @Override
        public void notifySlack(String channel, String message) {
            Map<String, String> msg = Map.of("text", message);
            slackPort.sendMessage(channel, msg);
        }
    }
}
