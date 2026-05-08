package com.example.domain.vforce;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 */
public class ReportDefectCommand implements Command {

    private final String defectId;
    private final String title;
    private final String githubUrl;

    public ReportDefectCommand(String defectId, String title, String githubUrl) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("githubUrl cannot be null or blank");
        }
        this.defectId = defectId;
        this.title = title;
        this.githubUrl = githubUrl;
    }

    public String getDefectId() {
        return defectId;
    }

    public String getTitle() {
        return title;
    }

    public String getGithubUrl() {
        return githubUrl;
    }
}
