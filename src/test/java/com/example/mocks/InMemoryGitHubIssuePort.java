package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class InMemoryGitHubIssuePort implements GitHubIssuePort {

  private String nextUrl = "https://github.com/default/repo/issues/1";
  private int callCount = 0;

  @Override
  public String createIssue(String title, String body) {
    callCount++;
    return nextUrl;
  }

  public void setNextIssueUrl(String url) { this.nextUrl = url; }

  public int getCallCount() { return callCount; }
}
