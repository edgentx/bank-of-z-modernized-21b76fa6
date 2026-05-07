package com.vforce360.ports;

/**
 * Port interface for fetching Modernization Assessment Reports.
 * Abstracts the storage mechanism (MongoDB/DB2) from the service layer.
 */
public interface MarPort {

    /**
     * Retrieves the raw MAR data for a specific project ID.
     *
     * @param projectId The UUID of the brownfield project.
     * @return A string containing the raw report content (JSON, Markdown, etc).
     * @throws IllegalArgumentException if the project is not found.
     */
    String getMarContent(String projectId);
}
