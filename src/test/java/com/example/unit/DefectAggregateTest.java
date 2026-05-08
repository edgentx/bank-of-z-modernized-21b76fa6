package com.example.unit;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DefectAggregateTest {

    @Test
    public void testReportDefectSuccess() {
        DefectAggregate aggregate = new DefectAggregate("DEF-001");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-001", 
            "Test Defect", 
            "Description", 
            "HIGH", 
            "auth", 
            "proj-1", 
            "https://github.com/test/repo/issues/1"
        );

        var events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertEquals("https://github.com/test/repo/issues/1", aggregate.getGithubIssueUrl());
    }

    @Test
    public void testReportDefectRequiresValidUrl() {
        DefectAggregate aggregate = new DefectAggregate("DEF-002");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "DEF-002", 
            "Test", 
            "Desc", 
            "LOW", 
            "comp", 
            "proj-1", 
            "" // Empty URL
        );

        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}