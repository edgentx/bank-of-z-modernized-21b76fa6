package com.example.workflows;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.SlackNotificationPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    void reportDefect(String defectId, String summary, String githubUrl);
}
