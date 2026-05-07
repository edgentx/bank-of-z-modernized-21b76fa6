package com.vforce360.ports;

import com.vforce360.model.ReportIdentifier;
import java.util.Optional;

/**
 * Port interface for accessing Modernization Assessment Reports.
 * Used by the service layer to retrieve report data regardless of storage implementation (Mongo/DB2).
 */
public interface IModernizationReportRepository {

    /**
     * Retrieves the raw report content for a specific project context.
     *
     * @param projectId The unique identifier of the project.
     * @return Optional containing the raw string data (JSON/Markdown), or empty if not found.
     */
    Optional<String> findRawContentByProjectId(String projectId);

    /**
     * Retrieves a report by its aggregate ID.
     *
     * @param reportId The report's unique identifier.
     * @return Optional containing the report data.
     */
    Optional<ReportData> findById(ReportIdentifier reportId);

    /**
     * Value object for report data.
     */
    record ReportData(String id, String projectId, String content) {}
}
