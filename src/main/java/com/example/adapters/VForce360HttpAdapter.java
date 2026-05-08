package com.example.adapters;

import com.example.ports.VForce360Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Real implementation of VForce360Port.
 * Sends HTTP POST requests to the Slack/VForce360 webhook endpoint.
 * <p>
 * In production, this uses {@link HttpURLConnection} to push the defect report.
 * For validation VW-454, the critical requirement is ensuring the GitHub URL
 * is injected into the message body sent to the external system.
 * </p>
 */
@Component
public class VForce360HttpAdapter implements VForce360Port {

    private static final Logger log = LoggerFactory.getLogger(VForce360HttpAdapter.class);
    private static final String WEBHOOK_URL_ENV = "VFORCE360_WEBHOOK_URL";

    @Override
    public void reportDefect(String defectTitle, String githubUrl) {
        try {
            // 1. Retrieve target URL from Environment (Standard Spring Boot pattern)
            String webhookUrl = System.getenv(WEBHOOK_URL_ENV);
            if (webhookUrl == null || webhookUrl.isBlank()) {
                log.warn("VForce360 Webhook URL is not configured (env: {}). Defect not reported: {}", WEBHOOK_URL_ENV, defectTitle);
                return;
            }

            // 2. Construct the Slack payload.
            // CRITICAL FIX FOR VW-454: Ensure 'githubUrl' is explicitly included in the message text.
            String slackBody = "Defect reported: " + defectTitle + "\nLink: " + githubUrl;
            String jsonPayload = "{\"text\": \"" + escapeJson(slackBody) + "\"}";

            // 3. Send HTTP Request
            URI uri = URI.create(webhookUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                log.info("Successfully reported defect '{}' to VForce360. Response: {}", defectTitle, responseCode);
            } else {
                log.error("Failed to report defect '{}'. Response code: {}", defectTitle, responseCode);
            }
            conn.disconnect();

        } catch (Exception e) {
            // In a temporal workflow, we might want to retry, but for the adapter we log the error.
            log.error("Error reporting defect '{}' to VForce360", defectTitle, e);
        }
    }

    /**
     * Basic JSON escaping to prevent breaking the payload structure.
     */
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
