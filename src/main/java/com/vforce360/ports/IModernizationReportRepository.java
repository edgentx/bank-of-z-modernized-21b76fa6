package com.vforce360.ports;

import java.util.Optional;

/**
 * Port interface for accessing Modernization Report data.
 * Decouples the service from the database implementation (MongoDB, DB2, etc.).
 */
public interface IModernizationReportRepository {

    /**
     * Finds a report by its identifier.
     *
     * @param reportId The ReportIdentifier (wrapper for UUID/String).
     * @return Optional containing ReportData if found.
     */
    Optional<ReportData> findById(ReportIdentifier reportId);
}
