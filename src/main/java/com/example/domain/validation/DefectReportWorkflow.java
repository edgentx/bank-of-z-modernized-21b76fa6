package com.example.domain.validation;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.shared.SlackMessageValidator;

public class DefectReportWorkflow {
    private final DefectRepository defectRepository;
    private final SlackMessageValidator slackMessageValidator;

    public DefectReportWorkflow(DefectRepository defectRepository, SlackMessageValidator slackMessageValidator) {
        this.defectRepository = defectRepository;
        this.slackMessageValidator = slackMessageValidator;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        aggregate.execute(cmd);
        defectRepository.save(aggregate);
    }

    public boolean validateMessage(String messageBody) {
        return slackMessageValidator.isValid(messageBody);
    }
}
