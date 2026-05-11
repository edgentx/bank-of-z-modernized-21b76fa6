package com.example.mocks;

import com.example.ports.SlackPort;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockSlackAdapter implements SlackPort {
    
    private final Map<String, String> sentMessages = new ConcurrentHashMap<>();
    private String lastSentMessage;
    
    @Override
    public boolean sendMessage(String channelId, String message) {
        if (channelId == null || channelId.isEmpty()) {
            throw new IllegalArgumentException("Channel ID cannot be null or empty");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        
        sentMessages.put(channelId, message);
        lastSentMessage = message;
        return true;
    }
    
    @Override
    public boolean sendBlocks(String channelId, Map<String, Object> blocks) {
        // Convert blocks to string representation
        String message = blocks.toString();
        return sendMessage(channelId, message);
    }
    
    @Override
    public String getLastSentMessage() {
        return lastSentMessage;
    }
    
    /**
     * Clear all sent messages (useful between tests)
     */
    public void clear() {
        sentMessages.clear();
        lastSentMessage = null;
    }
    
    /**
     * Get message by channel ID
     */
    public String getMessageByChannel(String channelId) {
        return sentMessages.get(channelId);
    }
}