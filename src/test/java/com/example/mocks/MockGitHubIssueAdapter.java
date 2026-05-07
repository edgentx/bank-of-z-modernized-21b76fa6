package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MockGitHubIssueAdapter implements GitHubIssuePort {
    private final List<CallLog> calls = new ArrayList<>();
    private URI nextResult;

    public static class CallLog {
        public final String title;
        public final String body;

        public CallLog(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    @Override
    public URI createIssue(String title, String body) {
        calls.add(new CallLog(title, body));
        // Default to a dummy URI if not set
        return nextResult != null ? nextResult : URI.create("https://github.com/example/issues/1");
    }

    public void setNextResult(URI uri) {
        this.nextResult = uri;
    }

    public List<CallLog> getCalls() {
        return calls;
    }
    
    public void reset() {
        calls.clear();
        nextResult = null;
    }
}