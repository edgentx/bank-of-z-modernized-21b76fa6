package com.example.ports;

import java.util.Map;

public interface GitHubPort {
    String createIssue(String title, String body, Map<String, String> labels);
}
