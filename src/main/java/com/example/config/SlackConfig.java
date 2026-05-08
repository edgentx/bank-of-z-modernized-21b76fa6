package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Slack integration.
 * Supports customization of the message template and link behavior.
 */
@Configuration
@ConfigurationProperties(prefix = "slack")
public class SlackConfig {

    /**
     * Template used to format the message body.
     * Supported placeholders: {title}, {url}
     */
    private String messageTemplate = "Defect Reported: {title}\nLink: <{url}|View Issue>";

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
}
