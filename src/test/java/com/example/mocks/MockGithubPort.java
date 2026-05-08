package com.example.mocks;

import com.example.ports.GithubPort;
import com.example.vforce.shared.ReportDefectCommand;
import java.util.ArrayList;
import java.util.List;

public class MockGithubPort implements GithubPort {
    public List<ReportDefectCommand> receivedCommands = new ArrayList<>();
    private String urlToReturn;

    public MockGithubPort(String urlToReturn) {
        this.urlToReturn = urlToReturn;
    }

    @Override
    public String createIssue(ReportDefectCommand command) {
        receivedCommands.add(command);
        return urlToReturn;
    }
}