package com.example.application;

import com.example.domain.shared.Command;
import com.example.domain.slack.ports.SlackNotifierPort;
import com.example.domain.vforce.ports.VForce360Port;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity definition for Defect Reporting.
 * Wraps the port interfaces for VForce360 and Slack.
 */
@ActivityInterface
public interface DefectReportingActivity {

    @ActivityMethod
    String reportToVForce360(String details);

    @ActivityMethod
    void notifySlack(String message);

    @Component
    @ActivityImpl(taskQueue = "DEFECT_TASK_QUEUE")
    class DefectReportingActivityImpl implements DefectReportingActivity {

        private final VForce360Port vForce360Port;
        private final SlackNotifierPort slackNotifierPort;

        @Autowired
        public DefectReportingActivityImpl(VForce360Port vForce360Port, SlackNotifierPort slackNotifierPort) {
            this.vForce360Port = vForce360Port;
            this.slackNotifierPort = slackNotifierPort;
        }

        @Override
        public String reportToVForce360(String details) {
            // We map the string details to the Command interface expected by the domain port
            // For this defect fix, we pass a dummy command or adapt the string.
            return vForce360Port.reportDefect(new Command() {});
        }

        @Override
        public void notifySlack(String message) {
            slackNotifierPort.sendNotification(message);
        }
    }
}
