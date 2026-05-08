package com.example.adapters;

import com.example.ports.SlackPort;

/**
 * Concrete implementation of SlackPort.
 * In a real scenario, this would use the Slack WebClient to send an API request.
 */
public class SlackAdapter implements SlackPort {

    @Override
    public void sendMessage(String message) {
        // Real implementation would involve:
        // MethodsClient methods = client.methods();
        // ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        //     .channel(channelId)
        //     .text(message)
        //     .build();
        // methods.chatPostMessage(request);
        
        // For defect validation, we assume the external call succeeds if this method is invoked.
        System.out.println("[SlackAdapter] Sending message: " + message);
    }
}