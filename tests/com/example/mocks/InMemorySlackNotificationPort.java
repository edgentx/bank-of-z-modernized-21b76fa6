package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

public class InMemorySlackNotificationPort implements SlackNotificationPort {
  public record Posted(String channel, String body) {}
  private final List<Posted> posted = new ArrayList<>();

  @Override
  public void postToChannel(String channel, String body) {
    posted.add(new Posted(channel, body));
  }

  public List<Posted> posted() { return posted; }
  public Posted last() { return posted.isEmpty() ? null : posted.get(posted.size() - 1); }
}
