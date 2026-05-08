package com.example.adapters;

import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of SlackNotifierPort.
 * In a real scenario, this would use WebClient or a Slack SDK to post the message.
 */
@Component
public class SlackNotifierAdapter implements SlackNotifierPort {

    // Autowire configuration or WebClient here
    // private final WebClient webClient;

    public SlackNotifierAdapter() {
        // Constructor injection of WebClient/RestTemplate would happen here
    }

    @Override
    public void postMessage(String messageBody) {
        // Pseudo-code for actual implementation:
        // if (messageBody == null) throw new IllegalArgumentException("Message body cannot be null");
        // webClient.post()
        //     .uri(slackWebhookUrl)
        //     .bodyValue(messageBody)
        //     .retrieve()
        //     .bodyToMono(Void.class)
        //     .block();
        
        // For the scope of this validation fix, we ensure the interface is satisfied
        // and logic contract matches the mock.
        if (messageBody == null) {
            throw new IllegalArgumentException("Message body cannot be null");
        }
        System.out.println("[SlackNotifier] Posting message: " + messageBody);
    }
}
