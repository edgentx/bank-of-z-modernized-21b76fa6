package com.example.ports;

/**
 * Port interface for external Defect tracking systems (e.g., GitHub Issues).
 * This is part of the Adapter pattern required by the build system.
 */
public interface DefectPort {
    /**
     * Creates a ticket in the external system (GitHub).
     * @param title The title of the defect.
     * @param description The description of the defect.
     * @return The URL of the created ticket.
     */
    String createExternalTicket(String title, String description);
}
