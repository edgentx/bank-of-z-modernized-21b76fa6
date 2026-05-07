package com.example.domain.vforce360.service;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WorkflowInterface
public interface VForce360Workflow {
    @WorkflowMethod
    String reportDefect(String defectId, String githubUrl, String slackBody);
}

@Component
@WorkflowImpl(taskQueues = "VForce360TaskQueue")
public class VForce360WorkflowImpl implements VForce360Workflow {

    @Autowired
    private VForce360Repository repository;

    @Override
    public String reportDefect(String defectId, String githubUrl, String slackBody) {
        VForce360Aggregate aggregate = repository.load(defectId);
        Command cmd = new ReportDefectCmd(defectId, githubUrl, slackBody);
        
        aggregate.execute(cmd);
        
        repository.save(aggregate);
        return aggregate.getGithubUrl();
    }
}
