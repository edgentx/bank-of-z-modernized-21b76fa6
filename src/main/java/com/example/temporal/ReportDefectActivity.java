package com.example.temporal;

import com.example.ports.SlackNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for reporting defects.
 * This activity is invoked by the Temporal workflow via temporal-worker exec.
 */
@Component
public class ReportDefectActivity {

    private static final Logger logger = LoggerFactory.getLogger(ReportDefectActivity.class);
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectActivity(SlackNotificationPort slackNotificationPort) {
        this.slackNotificationPort = slackNotificationPort;
    }

    /**
     * Reports a defect to the Slack channel.
     * Corresponds to '_report_defect' in the reproduction steps.
     * 
     * @param defectId The ID of the defect (e.g., "VW-454")
     * @param title The title of the defect
     * @return true if successfully reported, false otherwise.
     */
    public boolean reportDefect(String defectId, String title) {
        logger.info("Executing _report_defect for {} via temporal-worker exec", defectId);
        
        String channel = "#vforce360-issues";
        
        var result = slackNotificationPort.publishDefect(channel, title, defectId);
        
        if (result.isSuccess()) {
            logger.info("Defect {} reported successfully. Body: {}", defectId, result.getMessageBody());
        } else {
            logger.error("Failed to report defect {}", defectId);
        }
        
        return result.isSuccess();
    }
}
