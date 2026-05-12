package com.example.mocks;

import com.example.ports.SlackNotificationPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemorySlackNotificationPort implements SlackNotificationPort {

  private final List<String> messages = new ArrayList<>();

  @Override
  public void sendNotification(String message) {
    messages.add(message);
  }

  public String getLastMessage() {
    return messages.isEmpty() ? null : messages.get(messages.size() - 1);
  }

  public List<String> getAllMessages() {
    return Collections.unmodifiableList(messages);
  }

  public void clear() { messages.clear(); }
}
