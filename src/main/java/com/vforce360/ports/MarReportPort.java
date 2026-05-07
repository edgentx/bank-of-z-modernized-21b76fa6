package com.vforce360.ports;

import com.vforce360.models.MarReport;

import java.util.UUID;

/**
 * Port interface for accessing Modernization Assessment Reports.
 */
public interface MarReportPort {
    MarReport findById(UUID id);
}
