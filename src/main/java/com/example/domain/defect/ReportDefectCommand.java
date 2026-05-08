package com.example.domain.defect;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect. Migrated to POJO to support Java 11.
 */
public class ReportDefectCommand implements Command {
    private final String aggregateId;
    private final String summary;
    private final String description;
    private final Map<String, String> metadata;

    public ReportDefectCommand(String aggregateId, String summary, String description, Map<String, String> metadata) {
        this.aggregateId = aggregateId;
        this.summary = summary;
        this.description = description;
        this.metadata = metadata;
    }

    public String aggregateId() { return aggregateId; }
    public String summary() { return summary; }
    public String description() { return description; }
    public Map<String, String> metadata() { return metadata; }
}
