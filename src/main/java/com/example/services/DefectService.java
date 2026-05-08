package com.example.services;

import com.example.domain.shared.ValidationPort;
import com.example.domain.shared.SlackMessageValidator;
import com.example.domain.vforce360.model.DefectAggregate;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCommand;
import org.springframework.stereotype.Service;

@Service
public class DefectService {
    private final ValidationPort validationPort;
    private final SlackMessageValidator slackValidator;

    public DefectService(ValidationPort validationPort, SlackMessageValidator slackValidator) {
        this.validationPort = validationPort;
        this.slackValidator = slackValidator;
    }

    public DefectReportedEvent reportDefect(ReportDefectCommand cmd) {
        var aggregate = new DefectAggregate(cmd.defectId());
        var events = aggregate.execute(cmd);
        var event = (DefectReportedEvent) events.get(0);
        
        // Validation logic that previously failed
        validationPort.validate(event);
        
        return event;
    }
}
