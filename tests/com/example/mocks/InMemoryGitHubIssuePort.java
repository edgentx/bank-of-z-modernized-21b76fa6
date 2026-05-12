package com.example.mocks;

import com.example.ports.GitHubIssuePort;

public class InMemoryGitHubIssuePort implements GitHubIssuePort {
  private int seq = 0;
  private String lastTitle;
  private String lastUrl;

  @Override
  public String openIssue(String title, String body, String severity) {
    seq++;
    this.lastTitle = title;
    this.lastUrl = "https://github.com/egdcrypto/bank-of-z-modernized-21b76fa6/issues/" + seq;
    return lastUrl;
  }

  public String lastTitle() { return lastTitle; }
  public String lastUrl() { return lastUrl; }
}
