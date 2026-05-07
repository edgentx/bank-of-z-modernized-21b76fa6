package com.vforce360.ports;

import com.vforce360.model.MarReport;

/**
 * Port interface for retrieving MAR data.
 */
public interface MarReportPort {
    MarReport findByProjectId(String projectId);
}
